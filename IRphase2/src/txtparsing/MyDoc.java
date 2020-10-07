package txtparsing;

public class MyDoc {

    private String id;
    private String title;
    private String body;

    public MyDoc(String id, String title, String body){
        this.id=id;
        this.title=title;
        this.body=body;
    }

    @Override
    public String toString() {
        String ret = "MyDoc{"
                + "\n\tID: " + id
                + "\n\tTitle: " + title
                + "\n\tBody: " + body;
        return ret + "\n}";
    }

    //Getters & Setters
    public String getId() {return id;}
    public void setId(String id) {this.id=id;}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title=title;}

    public String getBody() {return body;}
    public void setBody(String body) {this.body=body;}

}
