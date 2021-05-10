import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataSiswa{

    public static class EmailAlreadyExistsException extends Exception {
        public EmailAlreadyExistsException(){
            super("Email Already Exists on Database");
        }
        public EmailAlreadyExistsException(String str){
            super("Email Already Exists on Database : " + str);
        }
        public String getMessage(){
            return super.getMessage();
        }
    }

    public static class Validator {

        public static class PasswordInvalidException extends Exception {
            public PasswordInvalidException(){
                super("Password is Invalid");
            }
            public PasswordInvalidException(String str){
                super("Password is Invalid : " + str);
            }
            public String getMessage(){
                return super.getMessage();
            }

        }

        public static class EmailInvalidException extends Exception {
            public EmailInvalidException(){
                super("Email is Invalid");
            }
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
    
    }

    public static class RombelValueInvalidException extends Exception{
        public RombelValueInvalidException(){
            super("Rombel Value is Invalid");
        }
        public RombelValueInvalidException(String str){
            super("Rombel Value is Invalid : " + str);
        }
        public String getMessage(){
            return super.getMessage();
        }
    }

    public static class UserNotFoundException extends Exception{
        public UserNotFoundException(){
            super("User Not Found/Password Does Not Match");
        }
        public UserNotFoundException(String str){
            super("User Not Found/Password Does Not Match : " + str);
        }
        public String getMessage(){
            return super.getMessage();
        }
    }

    public static class SemesterValueInvalidException extends Exception{
        public SemesterValueInvalidException(){
            super("Semester Value is Invalid");
        }
        public SemesterValueInvalidException(String str){
            super("Semester Value is Invalid : " + str);
        }
        public String getMessage(){
            return super.getMessage();
        }
    }
    
    public static class KelasValueInvalidException extends Exception{
        public KelasValueInvalidException(){
            super("Kelas Value is Invalid");
        }
        public KelasValueInvalidException(String str){
            super("Kelas Value is Invalid : " + str);
        }
        public String getMessage(){
            return super.getMessage();
        }
    }

    public static class DataCannotNullException extends Exception {
        public DataCannotNullException(){
            super("Data is Null caught on Exception");
        }
        public DataCannotNullException(String str){
            super("Data is Null caught on Exception on : " + str);
        }
        public String getMessage(){
            return super.getMessage();
        }
    }

    public static enum Kelas{
        NULL("Belum Dipilih", "(pilih salah satu)"), MIA("Matematika dan Ilmu Alam", "MIA"), IIS("Ilmu-ilmu Sosial", "IIS"), BHS("Sastra dan Bahasa", "BAHASA");
        private String str;
        private String shortStr;
        private int rombel;
        private Kelas(String str, String shortStr){
            this.str = str;
            this.shortStr = shortStr;
        }
        public void setRombel(int rombel){
            this.rombel = rombel;
        }
        public int getRombel(){
            return rombel;
        }
        public String toLongString(){
            return str;
        }
        public String toString(){
            return shortStr;
        }
    }

    public static void deleteByUser(String email, char[] passchar) throws DataSiswa.UserNotFoundException{
        if(!database.removeIf(fill -> {
            return fill.getEmail().equals(email) && fill.getPassHash().equals(digest(String.valueOf(passchar)));
        })) throw new DataSiswa.UserNotFoundException(email);
    }

    public static void updateByUser(String email, char[] passchar, String newnama, Kelas newkelas, int newrombel, int newsemester, String newcomment, String newemail, char[] newpasschar) throws DataSiswa.UserNotFoundException, DataSiswa.EmailAlreadyExistsException, DataSiswa.Validator.EmailInvalidException, DataSiswa.Validator.PasswordInvalidException, DataSiswa.SemesterValueInvalidException, DataSiswa.RombelValueInvalidException{
        DataSiswa ds = null;
        try{
            ds = database.stream().filter(fill -> fill.getEmail().equals(email) && fill.getPassHash().equals(digest(String.valueOf(passchar)))).findFirst().get();
        }catch(NoSuchElementException e){
            throw new DataSiswa.UserNotFoundException("On Update By User : " + email);
        }

        if(newnama != null) ds.setNama(newnama);
        if(newkelas != null) ds.setKelas(newkelas);
        if(newemail != null) ds.setEmail(newemail);
        if(newpasschar != null){
            ds.setPassword(String.valueOf(newpasschar));
        }
        if(newsemester != 0) ds.setSemester(newsemester);
        if(newrombel != 0) ds.setRombel(newrombel);
        if(newcomment != null) ds.setComment(newcomment);

    }

    public static List<DataSiswa> database = new ArrayList<DataSiswa>();
    private static int globalID = 0;
    public static final int maximumSemester = 6;

    private int id;
    private String nama;
    private Kelas kelas;
    private int semester;
    private int rombel;
    private String email;
    private String passHash;
    private String comment;

    public static String digest(String pass){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(pass.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void setId(int id){
        this.id = id;
    }

    public static void add(DataSiswa ds){
        ds.setId(globalID);
        globalID ++;
        database.add(ds);
        System.out.println("Adding data to database Successfully | next user ID is : " + globalID);
    }

    public DataSiswa(String nama, Kelas kelas,int rombel,  int semester, String email, char[] passChar) throws DataSiswa.Validator.PasswordInvalidException, DataSiswa.EmailAlreadyExistsException, DataSiswa.RombelValueInvalidException, DataSiswa.SemesterValueInvalidException, DataSiswa.Validator.EmailInvalidException, DataCannotNullException, KelasValueInvalidException {
        if(nama == null || nama.equals("")) throw new DataSiswa.DataCannotNullException("On Nama Entry");
        else this.nama = nama;
        if(kelas == Kelas.NULL) throw new DataSiswa.KelasValueInvalidException(nama);
        else this.kelas = kelas;
        if(semester > maximumSemester) throw new DataSiswa.SemesterValueInvalidException(nama);
        else this.semester = semester;
        if(rombel > kelas.getRombel()) throw new DataSiswa.RombelValueInvalidException(nama);
        else this.rombel = rombel;
        if(email == null || email.equals("")) throw new DataSiswa.DataCannotNullException("On Email Entry of : " + nama);
        if(!Validator.emailIsValid(email)) throw new DataSiswa.Validator.EmailInvalidException(nama);
        if(database.stream().anyMatch(p -> p.getEmail().equals(email))) throw new DataSiswa.EmailAlreadyExistsException(nama);
        else this.email = email;
        if(passChar == null || passChar.length == 0) throw new DataSiswa.DataCannotNullException("On Password Entry of : " + nama);
        String pass = String.copyValueOf(passChar);
        if(!Validator.passIsValid(pass)) throw new DataSiswa.Validator.PasswordInvalidException(nama);
        else passHash = digest(pass);
        if(comment == null) comment = "";
    }

    public DataSiswa(String nama, Kelas kelas,int rombel,  int semester, String email, char[] pass, String comment) throws DataSiswa.Validator.PasswordInvalidException, DataSiswa.EmailAlreadyExistsException, DataSiswa.RombelValueInvalidException, DataSiswa.SemesterValueInvalidException, DataSiswa.Validator.EmailInvalidException, DataCannotNullException, KelasValueInvalidException {
        this(nama,kelas,rombel,semester,email,pass);
        this.comment = comment;
    }

    public void setEmail(String email) throws DataSiswa.EmailAlreadyExistsException, DataSiswa.Validator.EmailInvalidException {
        if(!Validator.emailIsValid(email)) throw new DataSiswa.Validator.EmailInvalidException("Error on Set - " + nama);
        if(database.stream().anyMatch(p -> p.getEmail() == email)) throw new DataSiswa.EmailAlreadyExistsException("Error on Set - " + nama);
        this.email = email;
    }

    public void setKelas(Kelas kelas) {
        this.kelas = kelas;
    }

    public void setRombel(int rombel) throws DataSiswa.RombelValueInvalidException {
        if(rombel > kelas.getRombel()) throw new RombelValueInvalidException("Error on Set - " + nama);
        this.rombel = rombel;
    }

    public void setSemester(int semester) throws DataSiswa.SemesterValueInvalidException {
        if(semester > maximumSemester) throw new SemesterValueInvalidException("Error on Set - " + nama);
        this.semester = semester;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }
    
    public Kelas getKelas() {
        return kelas;
    }

    public String getNama() {
        return nama;
    }

    public int getRombel() {
        return rombel;
    }

    public int getSemester() {
        return semester;
    }

    public String getPassHash() {
        return passHash;
    }

    public String getComment() {
        return comment;
    }

    public void setPassword(String pass) throws DataSiswa.Validator.PasswordInvalidException {
        if(!Validator.passIsValid(pass)) throw new Validator.PasswordInvalidException("Error on Set - " + nama);
        passHash = digest(pass);
    }

    public Map<String, Object> getData(){
        return Map.of(
            "id", id,
            "nama", nama,
            "kelas",kelas,
            "semester", semester,
            "rombel",rombel,
            "email", email,
            "passhash", passHash,
            "comment", comment
        );
    }

}

