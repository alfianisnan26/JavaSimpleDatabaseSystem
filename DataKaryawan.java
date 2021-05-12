import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataKaryawan {

    public static class EmailAlreadyExistsException extends Exception {
        public EmailAlreadyExistsException(String str){
            super("Email Already Exists on Database : " + str);
        }
        public String getMessage(){
            return super.getMessage();
        }
    }

    public static class Validator {

        public static class DateValueInvalidException extends Exception{
            public DateValueInvalidException(String str){
                super("Date Value is Invalid : " + str);
            }
            public String getMessage(){
                return super.getMessage();
            }
        }

        public static class TimeValueInvalidException extends Exception{
            public TimeValueInvalidException(String str){
                super("Time Value is Invalid : " + str);
            }
            public String getMessage(){
                return super.getMessage();
            }
        }

        public static class PasswordInvalidException extends Exception {
            public PasswordInvalidException(String str){
                super("Password is Invalid : " + str);
            }
            public String getMessage(){
                return super.getMessage();
            }

        }

        public static class EmailInvalidException extends Exception {
            public EmailInvalidException(String str){
                super("Email is Invalid : " + str);
            }
            public String getMessage(){
                return super.getMessage();
            }
        }
        public static boolean passIsValid(final String password) {
            var patternPass = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,20}$");
            Matcher matcher = patternPass.matcher(password);
            return matcher.matches();
        }

        public static boolean emailIsValid(final String email) {
            var patternPass = Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
            Matcher matcher = patternPass.matcher(email);
            return matcher.matches();
        }

        public static boolean dateIsValid(final String date) {
            var patternPass = Pattern.compile("^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4}$");
            Matcher matcher = patternPass.matcher(date);
            return matcher.matches();
        }

        public static boolean timeIsValid(final String time) {
            var patternPass = Pattern.compile("([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]");
            Matcher matcher = patternPass.matcher(time);
            return matcher.matches();
        }



    }

    public static class UserNotFoundException extends Exception{
        public UserNotFoundException(String str){
            super("User Not Found/Password Does Not Match : " + str);
        }
        public String getMessage(){
            return super.getMessage();
        }
    }

    public static class DataCannotNullException extends Exception {
        public DataCannotNullException(String str){
            super(str);
        }
        public String getMessage(){
            return super.getMessage();
        }
    }

    public static void deleteByUser(String email, char[] passchar) throws DataKaryawan.UserNotFoundException{
        if(!database.removeIf(fill -> fill.getEmail().equals(email) && fill.getPassHash().equals(digest(String.valueOf(passchar))))) throw new DataKaryawan.UserNotFoundException(email);
    }

    public static void updateByUser(String email, char[] passchar, String newnama, String newtanggalMasuk, String newJamMasuk, String newJamKeluar, String newcomment, String newemail, char[] newpasschar) throws DataKaryawan.UserNotFoundException, DataKaryawan.EmailAlreadyExistsException, DataKaryawan.Validator.EmailInvalidException, DataKaryawan.Validator.PasswordInvalidException, Validator.TimeValueInvalidException, DataCannotNullException, Validator.DateValueInvalidException {
        DataKaryawan ds;
        try{
            ds = database.stream().filter(fill -> fill.getEmail().equals(email) && fill.getPassHash().equals(digest(String.valueOf(passchar)))).findAny().get();
        }catch(NoSuchElementException e){
            throw new DataKaryawan.UserNotFoundException("On Update By User : " + email);
        }

        if(newnama != null) ds.setNama(newnama);
        if(newtanggalMasuk != null) ds.setTanggalMasuk(newtanggalMasuk);
        if(newemail != null) ds.setEmail(newemail);
        if(newpasschar != null){
            ds.setPassword(String.valueOf(newpasschar));
        }
        if(newJamKeluar != null) ds.setJamKeluar(newJamKeluar);
        if(newJamMasuk != null) ds.setJamMasuk(newJamMasuk);
        if(newcomment != null) ds.setComment(newcomment);

    }

    public static List<DataKaryawan> database = new ArrayList<>();
    private static int globalID = 0;

    private int id;
    private String nama;
    private String tanggalMasuk;
    private String jamKeluar;
    private String jamMasuk;
    private String email;
    private String passHash;
    private String comment;

    public static String digest(String pass){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(pass.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            StringBuilder hashtext = new StringBuilder(no.toString(16));
            while (hashtext.length() < 32) {
                hashtext.insert(0, "0");
            }
            return hashtext.toString();
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void setId(int id){
        this.id = id;
    }

    public static void add(DataKaryawan ds){
        ds.setId(globalID);
        globalID ++;
        database.add(ds);
        System.out.println("Adding data to database Successfully | next user ID is : " + globalID);
    }

    public DataKaryawan(String nama, String tanggalMasuk, String jamMasuk, String jamKeluar, String email, char[] passChar) throws DataKaryawan.Validator.PasswordInvalidException, DataKaryawan.EmailAlreadyExistsException, DataKaryawan.Validator.EmailInvalidException, DataCannotNullException, Validator.DateValueInvalidException, Validator.TimeValueInvalidException {
        if(email == null || email.equals("")) throw new DataCannotNullException("Email Kosong");
        if(passChar == null || passChar.length <= 0) throw new DataCannotNullException("Password Kosong");
        if(nama == null || nama.equals(""))throw new DataKaryawan.DataCannotNullException("Nama Kosong");
        if(tanggalMasuk == null || tanggalMasuk.equals("") ) throw new DataCannotNullException("Tanggal Masuk Kosong");
        if(jamMasuk == null || jamMasuk.equals("")) throw new DataCannotNullException("Jam Masuk Kosong");
        if(jamKeluar == null || jamKeluar.equals("")) throw new DataCannotNullException("Jam Pulang Koson");
        if(!Validator.dateIsValid(tanggalMasuk)) throw new DataKaryawan.Validator.DateValueInvalidException(nama);
        if(!(Validator.timeIsValid(jamMasuk) && Validator.timeIsValid(jamKeluar))) throw new DataKaryawan.Validator.TimeValueInvalidException(nama);
        if(!Validator.emailIsValid(email)) throw new DataKaryawan.Validator.EmailInvalidException(nama);
        if(database.stream().anyMatch(p -> p.getEmail().equals(email))) throw new DataKaryawan.EmailAlreadyExistsException(nama);
        String pass = String.copyValueOf(passChar);
        if(!Validator.passIsValid(pass)) throw new DataKaryawan.Validator.PasswordInvalidException(nama);
        passHash = digest(pass);
        this.nama = nama;
        this.tanggalMasuk = tanggalMasuk;
        this.jamKeluar = jamKeluar;
        this.jamMasuk = jamMasuk;
        this.email = email;
        if(comment == null) comment = "";
    }

    public DataKaryawan(String nama, String tanggalMasuk, String jamMasuk, String jamKeluar, String email, char[] pass, String comment) throws DataKaryawan.Validator.PasswordInvalidException, DataKaryawan.EmailAlreadyExistsException, DataKaryawan.Validator.EmailInvalidException, DataCannotNullException, Validator.TimeValueInvalidException, Validator.DateValueInvalidException {
        this(nama,tanggalMasuk,jamMasuk,jamKeluar,email,pass);
        this.comment = comment;
    }

    public void setEmail(String email) throws DataKaryawan.EmailAlreadyExistsException, DataKaryawan.Validator.EmailInvalidException {
        if(!Validator.emailIsValid(email)) throw new DataKaryawan.Validator.EmailInvalidException("Error on Set - " + nama);
        if(database.stream().anyMatch(p -> p.getEmail().equals(email))) throw new DataKaryawan.EmailAlreadyExistsException("Error on Set - " + nama);
        this.email = email;
    }

    public void setTanggalMasuk(String tanggalMasuk) throws DataCannotNullException, Validator.DateValueInvalidException {
        if(tanggalMasuk == null || tanggalMasuk.equals("")) throw new DataKaryawan.DataCannotNullException("Tanggal Kosong");
        if(!Validator.dateIsValid(tanggalMasuk)) throw new DataKaryawan.Validator.DateValueInvalidException("Error on Set " + nama);
        this.tanggalMasuk = tanggalMasuk;
    }

    public void setJamMasuk(String jamMasuk) throws Validator.TimeValueInvalidException, DataCannotNullException {
        if(jamMasuk == null || jamMasuk.equals("")) throw new DataKaryawan.DataCannotNullException("Jam Masuk Kosong");
        if(!Validator.timeIsValid(jamMasuk)) throw new DataKaryawan.Validator.TimeValueInvalidException("Error on Set - " + nama);
        this.jamMasuk = jamMasuk;
    }

    public void setJamKeluar(String jamKeluar) throws Validator.TimeValueInvalidException, DataCannotNullException {
        if(jamKeluar == null || jamKeluar.equals("")) throw new DataKaryawan.DataCannotNullException("Jam Pulang Kosong");
        if(!Validator.timeIsValid(jamKeluar)) throw new DataKaryawan.Validator.TimeValueInvalidException("Error on Set - " + nama);
        this.jamKeluar = jamKeluar;
    }

    public void setNama(String nama) throws DataCannotNullException {
        if(nama == null || nama.equals("")) throw new DataKaryawan.DataCannotNullException("Nama Kosong");
        this.nama = nama;
    }

    public void setComment(String comment){
        this.comment = comment;
    }

    public String getEmail() {
        return email;
    }

    public String getPassHash() {
        return passHash;
    }

    public void setPassword(String pass) throws DataKaryawan.Validator.PasswordInvalidException, DataCannotNullException {
        if(pass == null) throw new DataKaryawan.DataCannotNullException("Password Kosong");
        if(!Validator.passIsValid(pass)) throw new Validator.PasswordInvalidException("Error on Set - " + nama);
        passHash = digest(pass);
    }

    public Map<String, Object> getData(){
        return Map.of(
            "id", id,
            "nama", nama,
            "tanggal_kerja",tanggalMasuk,
            "jam_masuk", jamMasuk,
            "jam_pulang",jamKeluar,
            "email", email,
            "passhash", passHash,
            "comment", comment
        );
    }

}

