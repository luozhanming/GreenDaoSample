package cn.luozhanming.greendaosample.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity
public class MatrixPen {

    @Id(autoincrement = true)
    private Long id;

    private String penId;

    private String penOwner;

    private Long classId;

    @ToOne(joinProperty = "classId")
    private Classroom ownerClass;

    @ToMany
    @JoinEntity(entity = QuestionToPen.class
            , sourceProperty = "penId"
            , targetProperty = "qId")
    private List<Question> questions;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1782590306)
    private transient MatrixPenDao myDao;

    @Generated(hash = 680056781)
    public MatrixPen(Long id, String penId, String penOwner, Long classId) {
        this.id = id;
        this.penId = penId;
        this.penOwner = penOwner;
        this.classId = classId;
    }

    @Generated(hash = 996421190)
    public MatrixPen() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPenId() {
        return this.penId;
    }

    public void setPenId(String penId) {
        this.penId = penId;
    }

    public String getPenOwner() {
        return this.penOwner;
    }

    public void setPenOwner(String penOwner) {
        this.penOwner = penOwner;
    }

    public Long getClassId() {
        return this.classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    @Generated(hash = 1371956132)
    private transient Long ownerClass__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1295026934)
    public Classroom getOwnerClass() {
        Long __key = this.classId;
        if (ownerClass__resolvedKey == null
                || !ownerClass__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ClassroomDao targetDao = daoSession.getClassroomDao();
            Classroom ownerClassNew = targetDao.load(__key);
            synchronized (this) {
                ownerClass = ownerClassNew;
                ownerClass__resolvedKey = __key;
            }
        }
        return ownerClass;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 912663921)
    public void setOwnerClass(Classroom ownerClass) {
        synchronized (this) {
            this.ownerClass = ownerClass;
            classId = ownerClass == null ? null : ownerClass.getId();
            ownerClass__resolvedKey = classId;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 871514844)
    public List<Question> getQuestions() {
        if (questions == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            QuestionDao targetDao = daoSession.getQuestionDao();
            List<Question> questionsNew = targetDao._queryMatrixPen_Questions(id);
            synchronized (this) {
                if (questions == null) {
                    questions = questionsNew;
                }
            }
        }
        return questions;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1619718141)
    public synchronized void resetQuestions() {
        questions = null;
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
    @Generated(hash = 1257437383)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMatrixPenDao() : null;
    }
}
