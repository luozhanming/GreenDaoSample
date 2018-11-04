package cn.luozhanming.greendaosample.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity
public class QuestionToPen {

    public static final int MASK_ANSWER_A = 1<<0;
    public static final int MASK_ANSWER_B = 1<<1;
    public static final int MASK_ANSWER_C = 1<<2;
    public static final int MASK_ANSWER_D = 1<<3;
    public static final int MASK_ANSWER_E = 1<<4;
    public static final int MASK_ANSWER_F = 1<<5;
    public static final int MASK_ANSWER_TRUE = 1<<6;
    public static final int MASK_ANSWER_FALSE = 1<<7;

    @Id(autoincrement = true)
    private Long id;

    private Long penId;

    @ToOne(joinProperty = "penId")
    private MatrixPen pen;

    private Long qId;

    @ToOne(joinProperty = "qId")
    private Question question;

    private int answer;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1367225381)
    private transient QuestionToPenDao myDao;

    @Generated(hash = 1005280234)
    public QuestionToPen(Long id, Long penId, Long qId, int answer) {
        this.id = id;
        this.penId = penId;
        this.qId = qId;
        this.answer = answer;
    }

    @Generated(hash = 941918759)
    public QuestionToPen() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPenId() {
        return this.penId;
    }

    public void setPenId(Long penId) {
        this.penId = penId;
    }

    public Long getQId() {
        return this.qId;
    }

    public void setQId(Long qId) {
        this.qId = qId;
    }

    public int getAnswer() {
        return this.answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    @Generated(hash = 254154139)
    private transient Long pen__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 451340867)
    public MatrixPen getPen() {
        Long __key = this.penId;
        if (pen__resolvedKey == null || !pen__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MatrixPenDao targetDao = daoSession.getMatrixPenDao();
            MatrixPen penNew = targetDao.load(__key);
            synchronized (this) {
                pen = penNew;
                pen__resolvedKey = __key;
            }
        }
        return pen;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 180208899)
    public void setPen(MatrixPen pen) {
        synchronized (this) {
            this.pen = pen;
            penId = pen == null ? null : pen.getId();
            pen__resolvedKey = penId;
        }
    }

    @Generated(hash = 527827701)
    private transient Long question__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 254136904)
    public Question getQuestion() {
        Long __key = this.qId;
        if (question__resolvedKey == null || !question__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            QuestionDao targetDao = daoSession.getQuestionDao();
            Question questionNew = targetDao.load(__key);
            synchronized (this) {
                question = questionNew;
                question__resolvedKey = __key;
            }
        }
        return question;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1119748818)
    public void setQuestion(Question question) {
        synchronized (this) {
            this.question = question;
            qId = question == null ? null : question.getId();
            question__resolvedKey = qId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 574826875)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getQuestionToPenDao() : null;
    }
}
