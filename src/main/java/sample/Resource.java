package sample;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Resource {

    private Type type;
    private String title;
    private Date date;
    private String description;


    enum Type{
        MedicationStatement,
        Observation
    }

    Resource(Type type, String title, Date date, String description) {
        this.type = type;
        this.title = title;
        this.date = date;
        this.description = description;
    }

    public Resource() {

    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public String getTime() {
        return new SimpleDateFormat("HH:mm").format(date);
    }

    public Type getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
