package cn.luozhanming.greendaosample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    Context appContext = InstrumentationRegistry.getTargetContext();

    @Test
    public void insertClassroom() {
        DaoMaster.DevOpenHelper mHelper = new DaoMaster.DevOpenHelper(appContext, "matrix_pen.db", null);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        DaoMaster mDaoMaster = new DaoMaster(db);
        DaoSession session = mDaoMaster.newSession();
        ClassroomDao classroomDao = session.getClassroomDao();
        List<Classroom> classrooms = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            Classroom classroom = new Classroom();
            classroom.setClassName("一年级" + i + "班");
            classroom.setClassId(i + "");
            classrooms.add(classroom);
        }
        classroomDao.insertInTx(classrooms);
    }

    @Test
    public void queryClassroom() {
        DaoMaster.DevOpenHelper mHelper = new DaoMaster.DevOpenHelper(appContext, "matrix_pen.db", null);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        DaoMaster mDaoMaster = new DaoMaster(db);
        DaoSession session = mDaoMaster.newSession();
        ClassroomDao classroomDao = session.getClassroomDao();
        List<Classroom> list = classroomDao.queryBuilder().list();
    }

    @Test
    public void insertPens() {
        AnswerStatisticsModel model = new AnswerStatisticsModel(appContext);
        DaoMaster.DevOpenHelper mHelper = new DaoMaster.DevOpenHelper(appContext, "matrix_pen.db", null);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        DaoMaster mDaoMaster = new DaoMaster(db);
        DaoSession session = mDaoMaster.newSession();
        MatrixPenDao matrixPenDao = session.getMatrixPenDao();
        matrixPenDao.deleteAll();
        List<MatrixPen> pens1 = new ArrayList<>();
        Classroom classroom1 = model.getClassroomByClassId("1");
        Classroom classroom2 = model.getClassroomByClassId("2");
        MatrixPen pen11 = new MatrixPen();
        pen11.setClassId(classroom1.getId());
        pen11.setPenOwner("小张");
        pen11.setPenId("F11111");
        pens1.add(pen11);
        MatrixPen pen12 = new MatrixPen();
        pen12.setClassId(classroom1.getId());
        pen12.setPenOwner("小黄");
        pen12.setPenId("F22222");
        pens1.add(pen12);
        MatrixPen pen13 = new MatrixPen();
        pen13.setClassId(classroom1.getId());
        pen13.setPenOwner("小刘");
        pen13.setPenId("F33333");
        pens1.add(pen13);

        MatrixPen pen21 = new MatrixPen();
        pen21.setClassId(classroom2.getId());
        pen21.setPenOwner("小智");
        pen21.setPenId("F111111");
        pens1.add(pen21);
        MatrixPen pen22 = new MatrixPen();
        pen22.setClassId(classroom2.getId());
        pen22.setPenOwner("小霞");
        pen22.setPenId("F222222");
        pens1.add(pen22);
        MatrixPen pen23 = new MatrixPen();
        pen23.setClassId(classroom2.getId());
        pen23.setPenOwner("小刚");
        pen23.setPenId("F333333");
        pens1.add(pen23);
        matrixPenDao.insertInTx(pens1);
        List<MatrixPen> list = matrixPenDao.queryBuilder().list();
        Classroom ownerClass = list.get(0).getOwnerClass();
    }

    @Test
    public void queryClassAndPens() {
        DaoMaster.DevOpenHelper mHelper = new DaoMaster.DevOpenHelper(appContext, "matrix_pen.db", null);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        DaoMaster mDaoMaster = new DaoMaster(db);
        DaoSession session = mDaoMaster.newSession();
        MatrixPenDao matrixPenDao = session.getMatrixPenDao();
        ClassroomDao classroomDao = session.getClassroomDao();
        List<Classroom> classrooms = classroomDao.queryBuilder().list();
        List<MatrixPen> pens1 = classrooms.get(0).getPens();
        List<MatrixPen> pens = matrixPenDao.queryBuilder().list();
        Classroom ownerClass = pens.get(0).getOwnerClass();
    }

    @Test
    public void insertQuestions() {
        DaoMaster.DevOpenHelper mHelper = new DaoMaster.DevOpenHelper(appContext, "matrix_pen.db", null);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        DaoMaster mDaoMaster = new DaoMaster(db);
        DaoSession session = mDaoMaster.newSession();
        QuestionDao questionDao = session.getQuestionDao();
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Question question = new Question();
            question.setIndex(i + 1);
            question.setPage(1);
            question.setType(Question.TYPE_SELECTION);
            question.setRightAnswer(1 << i);
            questions.add(question);
        }
        for (int i = 0; i < 2; i++) {
            Question question = new Question();
            question.setIndex(6 + i);
            question.setPage(1);
            question.setType(Question.TYPE_JUDGE);
            question.setRightAnswer(1 << (i + 6));
            questions.add(question);
        }
        questionDao.insertInTx(questions);
        List<Question> list = questionDao.queryBuilder().list();
    }

    @Test
    public void insertQuestionToPen(){
        DaoMaster.DevOpenHelper mHelper = new DaoMaster.DevOpenHelper(appContext, "matrix_pen.db", null);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        DaoMaster mDaoMaster = new DaoMaster(db);
        DaoSession session = mDaoMaster.newSession();
        QuestionToPenDao questionToPenDao = session.getQuestionToPenDao();
        QuestionDao questionDao = session.getQuestionDao();
        MatrixPenDao matrixPenDao = session.getMatrixPenDao();
        List<Question> questions = questionDao.queryBuilder().list();
        List<MatrixPen> pens = matrixPenDao.queryBuilder().list();
        List<QuestionToPen> qtps = new ArrayList<>();
        for (MatrixPen pen : pens) {
            for (Question question : questions) {
                QuestionToPen qtp = new QuestionToPen();
                qtp.setQId(question.getId());
                qtp.setPenId(pen.getId());
                qtp.setAnswer(QuestionToPen.MASK_ANSWER_A);
                qtps.add(qtp);
            }
        }
        questionToPenDao.insertInTx(qtps);
        List<MatrixPen> pens1 = questionDao.queryBuilder().list().get(0).getPens();
        List<Question> questions1 = matrixPenDao.queryBuilder().list().get(0).getQuestions();
        List<QuestionToPen> list = questionToPenDao.queryBuilder().list();
    }

    @Test
    public void testDomain(){
        AnswerStatisticsModel model = new AnswerStatisticsModel(appContext);
    }
}
