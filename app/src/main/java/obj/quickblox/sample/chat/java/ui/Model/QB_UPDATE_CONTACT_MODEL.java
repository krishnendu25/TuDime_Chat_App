package obj.quickblox.sample.chat.java.ui.Model;

public class QB_UPDATE_CONTACT_MODEL
{
    private String fullName,email,login,phone,website,password;
    private boolean Is_QBuser=false;

    public QB_UPDATE_CONTACT_MODEL()
    {

    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean get_Is_QBuser() {
        return Is_QBuser;
    }

    public void set_Is_QBuser(boolean is_QBuser) {
        Is_QBuser = is_QBuser;
    }
}