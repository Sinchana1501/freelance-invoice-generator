public class WorkLogDTO {
    private int     logId;
    private int     clientId;
    private String  description;
    private int     hours;
    private boolean billed;

    public WorkLogDTO() {}
    public WorkLogDTO(int logId, int clientId, String description, int hours, boolean billed) {
        this.logId = logId; this.clientId = clientId;
        this.description = description; this.hours = hours; this.billed = billed;
    }

    public int     getLogId()       { return logId; }
    public int     getClientId()    { return clientId; }
    public String  getDescription() { return description; }
    public int     getHours()       { return hours; }
    public boolean isBilled()       { return billed; }

    public void setLogId(int logId)              { this.logId = logId; }
    public void setClientId(int clientId)        { this.clientId = clientId; }
    public void setDescription(String d)         { this.description = d; }
    public void setHours(int hours)              { this.hours = hours; }
    public void setBilled(boolean billed)        { this.billed = billed; }
}
