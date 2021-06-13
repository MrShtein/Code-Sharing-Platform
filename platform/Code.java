package platform;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "code")
public class Code {

    @Id
    private String id;
    private String code;
    private String loadDate;
    private long views;
    private long time;
    private long restrictedTime;
    private boolean isTimeRestricted;
    private boolean isViewsRestricted;

    public Code(String id, String code, String loadDate, long views, long time) {
        this.id = id;
        this.code = code;
        this.loadDate = loadDate;
        this.views = views;
        this.time = time;
    }

    public Code() {

    }

    @Column(name = "time_left")
    public long getRestrictedTime() {
        return restrictedTime;
    }

    public void setRestrictedTime(long timeLeft) {
        this.restrictedTime = timeLeft;
    }

    public String getId() {
        return id;
    }

    @Column(name = "load_date", nullable = true)
    public String getLoadDate() {
        return loadDate;
    }

    public void setLoadDate(String date) {
        this.loadDate = date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "VIEWS", nullable = true)
    public long getViews() {
        return views;
    }

    @Column(name = "IS_TIME_RESTRICTED", nullable = true)
    public boolean isTimeRestricted() {
        return isTimeRestricted;
    }

    @Column(name = "IS_VIEW_RESTRICTED", nullable = true)
    public boolean isViewsRestricted() {
        return isViewsRestricted;
    }

    public void setTimeRestricted(boolean timeRestricted) {
        this.isTimeRestricted = timeRestricted;
    }

    public void setViewsRestricted(boolean viewsRestricted) {
        this.isViewsRestricted = viewsRestricted;
    }

    public void setViews(long viewsRestriction) {
        this.views = viewsRestriction;
    }

    @Column(name = "TIME")
    public long getTime() {
        return time;
    }

    public void setTime(long secondsRestriction) {
        this.time = secondsRestriction;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
