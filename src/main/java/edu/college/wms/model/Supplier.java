package edu.college.wms.model;

public class Supplier {
    private int id;
    private String name;
    private String phone;
    private String email;
    private String company;
    private String address;

    public Supplier(int id, String name, String phone, String email, String company, String address) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.company = company;
        this.address = address;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getCompany() { return company; }
    public String getAddress() { return address; }
    public void setId(int id) { this.id = id; }
}
