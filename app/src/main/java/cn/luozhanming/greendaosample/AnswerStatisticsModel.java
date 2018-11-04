package cn.luozhanming.greendaosample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.HashMap;
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

    public Question getQuestionByIndex(int index) {
        List<Question> list = questionDao.queryBuilder().where(QuestionDao.Properties.Index.eq(index)).list();
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
     * 根据课室查询每道题的正确率
     * */
    public float[] queryClassroomAccuracy(String classId) {
        int questionSize = (int) questionDao.queryBuilder().count();
        float ret[] = new float[questionSize];
        int penCountForClass = (int) matrixPenDao.queryBuilder().where(MatrixPenDao.Properties.ClassId.eq(classId)).count();
        for (int i = 0; i < questionSize; i++) {
            //1.查询题
            Question question = questionDao.queryBuilder().where(QuestionDao.Properties.Index.eq(i + 1)).unique();
            QueryBuilder<QuestionToPen> queryBuilder = questionToPenDao.queryBuilder();
            queryBuilder.where(QuestionToPenDao.Properties.QId.eq(question.getId()));
            queryBuilder.where(QuestionToPenDao.Properties.Answer.eq(question.getRightAnswer()));
            List<QuestionToPen> list = queryBuilder.list();
            int answerRightCount = 0;
            for (QuestionToPen questionToPen : list) {
                MatrixPen pen = questionToPen.getPen();
                if (classId.equals(pen.getClassId())) answerRightCount++;
            }
            ret[i] = (float) answerRightCount / penCountForClass;
        }
        return ret;
    }

    public Map<String,Float> queryQuestionAccuracyForEachClassroom(int index){
        Map<String,Float> retMap = new HashMap<>();
        Question question = questionDao.queryBuilder().where(QuestionDao.Properties.Index.eq(index)).unique();
        List<Classroom> classrooms = classroomDao.queryBuilder().list();
        QueryBuilder<QuestionToPen> queryBuilder = questionToPenDao.queryBuilder();
        queryBuilder.where(QuestionToPenDao.Properties.QId.eq(question.getId()));
        queryBuilder.where(QuestionToPenDao.Properties.Answer.eq(question.getRightAnswer()));
        List<QuestionToPen> list = queryBuilder.list();
        int classSize = classrooms.size();
        Map<String,AtomicInteger> correctMap = new HashMap<>();
        Map<String,Integer> totalMap = new HashMap<>();
        for (int i = 0; i < classSize; i++) {
            Classroom classroom = classrooms.get(i);
            correctMap.put(classroom.getClassId(),new AtomicInteger());
            int count = (int) matrixPenDao.queryBuilder().where(MatrixPenDao.Properties.ClassId.eq(classroom.getClassId())).count();
            totalMap.put(classroom.getClassId(),count);
        }
        for (QuestionToPen questionToPen : list) {
            MatrixPen pen = questionToPen.getPen();
            Classroom ownerClass = pen.getOwnerClass();
            correctMap.get(ownerClass.getClassId()).addAndGet(1);
        }
        for (Map.Entry<String, AtomicInteger> correctEntry : correctMap.entrySet()) {
            String classKey = correctEntry.getKey();
            int correct = correctEntry.getValue().get();
            int total = totalMap.get(classKey);
            retMap.put(classKey,(float)correct/total);
        }
        return retMap;

    }
}
