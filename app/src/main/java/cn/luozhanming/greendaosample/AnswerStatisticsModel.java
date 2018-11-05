package cn.luozhanming.greendaosample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import cn.luozhanming.greendaosample.entity.Classroom;
import cn.luozhanming.greendaosample.entity.ClassroomDao;
import cn.luozhanming.greendaosample.entity.DaoMaster;
import cn.luozhanming.greendaosample.entity.DaoSession;
import cn.luozhanming.greendaosample.entity.MatrixPen;
import cn.luozhanming.greendaosample.entity.MatrixPenDao;
import cn.luozhanming.greendaosample.entity.Question;
import cn.luozhanming.greendaosample.entity.QuestionDao;
import cn.luozhanming.greendaosample.entity.QuestionToPen;
import cn.luozhanming.greendaosample.entity.QuestionToPenDao;

public class AnswerStatisticsModel {

    private DaoMaster.DevOpenHelper mHelper;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    private ClassroomDao classroomDao;
    private MatrixPenDao matrixPenDao;
    private QuestionDao questionDao;
    private QuestionToPenDao questionToPenDao;


    public AnswerStatisticsModel(Context context) {
        mHelper = new DaoMaster.DevOpenHelper(context, "matrix_pen.db", null);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
        classroomDao = mDaoSession.getClassroomDao();
        matrixPenDao = mDaoSession.getMatrixPenDao();
        questionDao = mDaoSession.getQuestionDao();
        questionToPenDao = mDaoSession.getQuestionToPenDao();
    }


    /**
     * 插入课室数据
     * */
    public void insertClassroomData(List<Classroom> classrooms){
        classroomDao.insertInTx(classrooms);
    }

    /**
     * 插入点阵笔数据
     * */
    public void insertMatrixPenData(List<MatrixPen> pens){
        matrixPenDao.insertInTx(pens);
    }

    /**
     * 插入题目数据
     * */
    public void insertQuestionsData(List<Question> questions){
        questionDao.insertInTx(questions);
    }

    /**
     * 根据题目和点阵笔关联
     * */
    public void bindQuestionToPen(){
        List<Question> questions = questionDao.queryBuilder().list();
        List<MatrixPen> pens = matrixPenDao.queryBuilder().list();
        List<QuestionToPen> qtps = new ArrayList<>();
        for (MatrixPen pen : pens) {
            for (Question question : questions) {
                QuestionToPen qtp = new QuestionToPen(pen.getId(),question.getId(),0);
                qtps.add(qtp);
            }
        }
        questionToPenDao.insertInTx(qtps);
    }

    public MatrixPen getPenById(String penId) {
        List<MatrixPen> pens = matrixPenDao.queryBuilder().where(MatrixPenDao.Properties.PenId.eq(penId)).list();
        if (pens == null || pens.size() == 0) return null;
        else return pens.get(0);
    }

    public Classroom getClassroomByClassId(String classId) {
        List<Classroom> classrooms = classroomDao.queryBuilder().where(ClassroomDao.Properties.ClassId.eq(classId)).list();
        if (classrooms == null || classrooms.size() == 0) return null;
        return classrooms.get(0);
    }


    /**
     * 获得某页某类型题目数量
     *
     * @param type 类型
     * @param page
     */
    public int getQuestionCount(int type, int page) {
        return (int) questionDao.queryBuilder()
                .where(QuestionDao.Properties.Type.eq(type), QuestionDao.Properties.Page.eq(page))
                .count();
    }

    /**
     * 根据题号和所属页码查询题目
     *
     * @param index
     * @param page
     * @return
     */
    public Question getQuestionByIndex(int index, int page) {
        List<Question> list = questionDao.queryBuilder().where(QuestionDao.Properties.Index.eq(index), QuestionDao.Properties.Page.eq(page)).list();
        if (list == null || list.size() == 0) return null;
        return list.get(0);
    }


    /**
     * 查询某班的笔
     *
     * @param classId 课室id
     */
    public List<MatrixPen> queryPensByClass(String classId) {
        //1.找出classId相等的课室
        Classroom classroom = getClassroomByClassId(classId);
        List<MatrixPen> ret = matrixPenDao.queryBuilder().where(MatrixPenDao.Properties.ClassId.eq(classroom.getClassId())).list();
        return ret;
    }


    /**
     * 通过penId查询所属课室
     */
    public Classroom queryClassroomByPen(String penId) {
        MatrixPen pen = getPenById(penId);
        return pen.getOwnerClass();
    }

    /**
     * 获得某笔的所有题目
     */
    public List<Question> queryQuestionsForPenAnswer(String penId) {
        MatrixPen pen = getPenById(penId);
        return pen.getQuestions();
    }

    /**
     * 获取某题型的题目
     */
    public List<Question> queryQuestionsByType(int type, int page) {
        return questionDao.queryBuilder()
                .where(QuestionDao.Properties.Type.eq(type), QuestionDao.Properties.Page.eq(page))
                .list();
    }


    /**
     * 根据课室查询某页的某类型所有题目的正确率(已验证)
     *
     * @param classId 课室id
     * @return 每道题的正确率
     */
    public Map<Question, Float> queryClassroomAccuracy(String classId, int page, int type) {
        Map<Question, Float> retMap = new LinkedHashMap<>();
        List<Question> questions = questionDao.queryBuilder()
                .where(QuestionDao.Properties.Page.eq(page), QuestionDao.Properties.Type.eq(type))
                .orderAsc(QuestionDao.Properties.Index)
                .list();
        int questionSize = questions.size();
        int penCountForClass = (int) matrixPenDao.queryBuilder().where(MatrixPenDao.Properties.ClassId.eq(classId)).count();
        for (int i = 0; i < questionSize; i++) {
            //1.查询题
            Question question = questions.get(i);
            // Question question = questionDao.queryBuilder().where(QuestionDao.Properties.Index.eq(i + 1)).unique();
            QueryBuilder<QuestionToPen> queryBuilder = questionToPenDao.queryBuilder();
            queryBuilder.where(QuestionToPenDao.Properties.QId.eq(question.getId()));
            queryBuilder.where(QuestionToPenDao.Properties.Answer.eq(question.getRightAnswer()));
            List<QuestionToPen> list = queryBuilder.list();
            int answerRightCount = 0;
            for (QuestionToPen questionToPen : list) {
                MatrixPen pen = questionToPen.getPen();
                if (classId.equals(pen.getOwnerClass().getClassId()))
                    answerRightCount++;
            }
            float ret = (float) answerRightCount / penCountForClass;
            retMap.put(question, ret);
        }
        return retMap;
    }

    /**
     * 根据题号查询每班的正确率(已验证)
     *
     * @param index 题号
     * @return key:课室id value正确率
     */
    public Map<Classroom, Float> queryQuestionAccuracyForEachClassroom(int index, int page) {
        Map<Classroom, Float> retMap = new LinkedHashMap<>();
        Question question = questionDao.queryBuilder().where(QuestionDao.Properties.Index.eq(index)
                , QuestionDao.Properties.Page.eq(page)).unique();
        List<Classroom> classrooms = classroomDao.queryBuilder().list();
        QueryBuilder<QuestionToPen> queryBuilder = questionToPenDao.queryBuilder();
        queryBuilder.where(QuestionToPenDao.Properties.QId.eq(question.getId()));
        queryBuilder.where(QuestionToPenDao.Properties.Answer.eq(question.getRightAnswer()));
        List<QuestionToPen> list = queryBuilder.list();
        int classSize = classrooms.size();
        Map<Classroom, AtomicInteger> correctMap = new LinkedHashMap<>();
        Map<Classroom, Integer> totalMap = new LinkedHashMap<>();
        for (int i = 0; i < classSize; i++) {
            Classroom classroom = classrooms.get(i);
            correctMap.put(classroom, new AtomicInteger());
            int count = (int) matrixPenDao.queryBuilder().where(MatrixPenDao.Properties.ClassId.eq(classroom.getClassId())).count();
            totalMap.put(classroom, count);
        }
        for (QuestionToPen questionToPen : list) {
            MatrixPen pen = questionToPen.getPen();
            Classroom ownerClass = pen.getOwnerClass();
            correctMap.get(ownerClass).addAndGet(1);
        }
        for (Map.Entry<Classroom, AtomicInteger> correctEntry : correctMap.entrySet()) {
            Classroom classKey = correctEntry.getKey();
            int correct = correctEntry.getValue().get();
            int total = totalMap.get(classKey);
            retMap.put(classKey, (float) correct / total);
        }
        return retMap;
    }


    /**
     * 根据题号查询正确率(已验证)
     *
     * @param index 题号
     * @return 正确率
     */
    public float computeAccuracyForQuestion(int index, int page) {
        Question question = questionDao.queryBuilder().where(QuestionDao.Properties.Index.eq(index), QuestionDao.Properties.Page.eq(page)).unique();
        int answerTotal = (int) questionToPenDao.queryBuilder().where(QuestionToPenDao.Properties.QId.eq(question.getId())).count();
        int correctCount = (int) questionToPenDao.queryBuilder().where(QuestionToPenDao.Properties.QId.eq(question.getId()), QuestionToPenDao.Properties.Answer.eq(question.getRightAnswer())).count();
        float accuracy = (float) correctCount / answerTotal;
        return accuracy;
    }

    /**
     * 根据课室查看某题目答对/错的人(已验证)
     *
     * @return key:答对/答错  value：答对/答错的人
     */
    public Map<Boolean, List<MatrixPen>> queryPenWithCorrectAnswer(String classId, int page, int index) {
        Map<Boolean, List<MatrixPen>> retMap = new HashMap<>();
        List<MatrixPen> correctPen = new ArrayList<>();
        List<MatrixPen> incorrectPen = new ArrayList<>();
        retMap.put(false, incorrectPen);
        retMap.put(true, correctPen);
        Classroom classroom = getClassroomByClassId(classId);
        Question question = questionDao.queryBuilder()
                .where(QuestionDao.Properties.Index.eq(index), QuestionDao.Properties.Page.eq(page))
                .unique();
        List<MatrixPen> pens = classroom.getPens();
        for (MatrixPen pen : pens) {
            QuestionToPen questionToPen = questionToPenDao.queryBuilder()
                    .where(QuestionToPenDao.Properties.PenId.eq(pen.getId())
                            , QuestionToPenDao.Properties.QId.eq(question.getId())).unique();
            if (questionToPen.getAnswer() == question.getRightAnswer()) {
                correctPen.add(pen);
            } else {
                incorrectPen.add(pen);
            }
        }
        return retMap;
    }
}
