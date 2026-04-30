public class ClientDTO {
    private int    id;
    private String name;
    private int    hourlyRate;

    public ClientDTO() {}
    public ClientDTO(int id, String name, int hourlyRate) {
        this.id = id; this.name = name; this.hourlyRate = hourlyRate;
    }

    public int    getId()          { return id; }
    public String getName()        { return name; }
    public int    getHourlyRate()  { return hourlyRate; }

    public void setId(int id)              { this.id = id; }
    public void setName(String name)       { this.name = name; }
    public void setHourlyRate(int r)       { this.hourlyRate = r; }

    @Override public String toString() { return id + " – " + name; }
}
