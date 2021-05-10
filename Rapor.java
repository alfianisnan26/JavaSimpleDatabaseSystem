import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;

public class Rapor extends JFrame {
    private JTable table;
    private JRadioButton addRadioButton;
    private JRadioButton updateRadioButton;
    private JRadioButton deleteRadioButton;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField namaField;
    private JCheckBox namaCheck;
    private JComboBox<DataSiswa.Kelas> kelasField;
    private JCheckBox kelasCheck;
    private JComboBox<Integer> rombelField;
    private JCheckBox rombelCheck;
    private JComboBox semesterField;
    private JCheckBox semesterCheck;
    private JTextField commentField;
    private JCheckBox commentCheck;
    private JPasswordField newPasswordField;
    private JCheckBox newPasswordCheck;
    private JCheckBox newEmailCheck;
    private JTextField newEmailField;
    private JButton submitButton;
    private JPanel mainPanel;
    private JPanel leftPanel;
    private JPanel operationPanel;
    private JPanel formPanel;
    private JPanel submitPanel;
    private JPanel rightPanel;
    private JScrollPane tableScrollPane;
    private JLabel emailLabel;
    private JLabel passwordLabel;
    private JLabel namaLabel;
    private JLabel kelasLabel;
    private JLabel rombelLabel;
    private JLabel semesterLabel;
    private JLabel commentLabel;
    private JLabel newEmailLabel;
    private JLabel newPasswordLabel;
    private JCheckBox passwordCheck;
    private JCheckBox emailCheck;
    private JPasswordField passwordConfirmField;
    private JCheckBox passwordConfirmCheck;
    private JLabel passwordConfirmLabel;
    private final DefaultTableModel dm = new DefaultTableModel(){
        public boolean isCellEditable(int row, int column)
        {
            return false;
        }
    };
    private final String[] columnNames = {
            "id",
            "nama",
            "kelas",
            "rombel",
            "semester",
            "email",
            "pass",
            "comment"
    };

    OperationState state = OperationState.ADD;

    @FunctionalInterface
    public static interface SimpleDocumentListener extends DocumentListener {
        void update(DocumentEvent e);

        @Override
        default void insertUpdate(DocumentEvent e) {
            update(e);
        }
        @Override
        default void removeUpdate(DocumentEvent e) {
            update(e);
        }
        @Override
        default void changedUpdate(DocumentEvent e) {
            update(e);
        }
    }

    private void setObjectEnable(JCheckBox check, boolean checkState, boolean selected){
        check.setEnabled(checkState);
        check.setSelected(selected);
    }

    private void createCheckListener(JComponent field, JLabel label, JCheckBox check){
        check.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean state = (e.getStateChange() == 1);
                field.setEnabled(state);
                label.setEnabled(state);
            }
        });
    }

    private void updateFormState(OperationState state){
        final boolean checkState = (state == OperationState.UPDATE);
        final boolean selected = (state == OperationState.ADD);
        setObjectEnable(namaCheck, checkState, selected);
        setObjectEnable(kelasCheck, checkState, selected);
        setObjectEnable(semesterCheck, checkState, selected);
        setObjectEnable(rombelCheck, checkState, selected);
        setObjectEnable(newEmailCheck, checkState, selected);
        setObjectEnable(newPasswordCheck, checkState, selected);
        setObjectEnable(commentCheck, checkState, selected);
        passwordField.setText("");
        passwordConfirmField.setText("");
        setObjectEnable(passwordConfirmCheck, false, false);
        passwordConfirmField.setEnabled(state == OperationState.ADD);
        passwordConfirmLabel.setEnabled(state == OperationState.ADD);
    }

    private void createRadioListener(JRadioButton radio, OperationState setState){
        radio.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                state = setState;
                updateFormState(state);
                System.out.println("Database State = " + state);
            }
        });
    }

    private void createKelasListener(){
        kelasField.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == 1) {
                    rombelField.removeAllItems();
                    for(int a = 1 ; a <= ((DataSiswa.Kelas) e.getItem()).getRombel() ; a++)
                        rombelField.addItem(a);
                }
            }
        });
    }

    private void createValidatorListener(){
        passwordField.getDocument().addDocumentListener((SimpleDocumentListener) e ->
            passwordCheck.setSelected(DataSiswa.Validator.passIsValid(String.valueOf(passwordField.getPassword()))));
        passwordConfirmField.getDocument().addDocumentListener((SimpleDocumentListener) e ->
            passwordConfirmCheck.setSelected(Arrays.equals(passwordField.getPassword(), passwordConfirmField.getPassword())));
        emailField.getDocument().addDocumentListener((SimpleDocumentListener) e ->
            emailCheck.setSelected(DataSiswa.Validator.emailIsValid(emailField.getText())));
    }

    private void createSubmitListener(){
        submitButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                switch(state){
                    case ADD:{
                        if(!passwordConfirmCheck.isSelected()) {
                            JOptionPane.showMessageDialog(null, "Password Konfirmasi tidak sesuai, pastikan password yang anda masukkan sesuai");
                            return;
                        }
                        try {
                            DataSiswa ds = new DataSiswa(namaField.getText(), (DataSiswa.Kelas) kelasField.getSelectedItem(), (int) rombelField.getSelectedItem(), (int) semesterField.getSelectedItem(), emailField.getText(), passwordField.getPassword(), commentField.getText());
                            DataSiswa.add(ds);
                        } catch ( DataSiswa.Validator.PasswordInvalidException err){
                            JOptionPane.showMessageDialog(null, "Password tidak dapat diterima, pastikan password memiliki minimal satu huruf besar, satu huruf kecil, satu angka, dan berjumlah antara 8 sampai 20 karakter");
                        } catch (DataSiswa.EmailAlreadyExistsException err){
                            JOptionPane.showMessageDialog(null, "Email sudah dipakai oleh pengguna lain, mohon masukkan email lainnya");
                        } catch (DataSiswa.RombelValueInvalidException err){
                            JOptionPane.showMessageDialog(null, "Rombel tidak dapat diterima, pastikan nilai rombel sesuai dengan kriteria");
                        } catch (DataSiswa.SemesterValueInvalidException err){
                            JOptionPane.showMessageDialog(null, "Semester tidak dapat diterima, pastikan nilai semester sesuai dengan kriteria");
                        } catch (DataSiswa.Validator.EmailInvalidException err){
                            JOptionPane.showMessageDialog(null, "Email tidak dapat diterima, pastikan email sesuai dengan kriteria");
                        } catch (DataSiswa.KelasValueInvalidException err) {
                            JOptionPane.showMessageDialog(null, "Kelas tidak dapat diterima, pastikan nilai kelas sesuai dengan kriteria");
                        } catch (NullPointerException | DataSiswa.DataCannotNullException err){
                            JOptionPane.showMessageDialog(null, "Pastikan anda mengisi atau memilih entri data dengan benar");
                        } catch (Exception err){
                            JOptionPane.showMessageDialog(null, "Error Tidak Dikenal : " + err.getMessage());
                        }
                        break;
                    }
                    case UPDATE:{
                        String newNama = null;
                        DataSiswa.Kelas newKelas = null;
                        int newRombel = 0;
                        int newSemester = 0;
                        String newComment = null;
                        String newEmail = null;
                        char[] newPassword = null;
                        if(namaCheck.isSelected()) newNama = namaField.getText();
                        if(kelasCheck.isSelected()) newKelas = (DataSiswa.Kelas) kelasField.getSelectedItem();
                        if(rombelCheck.isSelected())
                            try {
                                newRombel = (int) rombelField.getSelectedItem();
                            } catch (NullPointerException ex){
                                JOptionPane.showMessageDialog(null, "Nilai Rombel tidak dikenal! Pastikan anda mengisi atau memilih entri data dengan benar");
                            }
                        if(semesterCheck.isSelected())
                            try {
                                newSemester = (int) semesterField.getSelectedItem();
                            } catch(NullPointerException ex){
                                JOptionPane.showMessageDialog(null, "Nilai Semester tidak dikenal! Pastikan anda mengisi atau memilih entri data dengan benar");
                            }
                        if(commentCheck.isSelected()) newComment = commentField.getText();
                        if(newEmailCheck.isSelected()) newEmail = newEmailField.getText();
                        if(newPasswordCheck.isSelected()) newPassword = newPasswordField.getPassword();
                        try {
                            DataSiswa.updateByUser(emailField.getText(), passwordField.getPassword(), newNama, newKelas, newRombel, newSemester, newComment, newEmail, newPassword);
                        } catch (DataSiswa.UserNotFoundException userNotFoundException) {
                            JOptionPane.showMessageDialog(null, "Pengguna tidak ditemukan atau Password salah, mohon cek kembali");
                        } catch (DataSiswa.EmailAlreadyExistsException emailAlreadyExistsException) {
                            JOptionPane.showMessageDialog(null, "Email sudah dipakai oleh pengguna lain, mohon masukkan email lainnya");
                        } catch (DataSiswa.Validator.EmailInvalidException emailInvalidException) {
                            JOptionPane.showMessageDialog(null, "Email tidak dapat diterima, pastikan email sesuai dengan kriteria");
                        } catch (DataSiswa.Validator.PasswordInvalidException passwordInvalidException) {
                            JOptionPane.showMessageDialog(null, "Password tidak dapat diterima, pastikan password memiliki minimal satu huruf besar, satu huruf kecil, satu angka, dan berjumlah antara 8 sampai 20 karakter");
                        } catch (DataSiswa.SemesterValueInvalidException semesterValueInvalidException) {
                            JOptionPane.showMessageDialog(null, "Semester tidak dapat diterima, pastikan nilai semester sesuai dengan kriteria");
                        } catch (DataSiswa.RombelValueInvalidException rombelValueInvalidException) {
                            JOptionPane.showMessageDialog(null, "Rombel tidak dapat diterima, pastikan nilai rombel sesuai dengan kriteria");
                        }
                        break;
                    }
                    case DELETE:{
                        try {
                            DataSiswa.deleteByUser(emailField.getText(), passwordField.getPassword());
                        } catch (DataSiswa.UserNotFoundException userNotFoundException) {
                            JOptionPane.showMessageDialog(null, "Pengguna tidak ditemukan atau Password salah, mohon cek kembali");
                        }
                        break;
                    }
                }
                Object[][] data = new Object[DataSiswa.database.size()][columnNames.length];

                for(int a = 0; a < DataSiswa.database.size() ; a++){
                    for(int b = 0; b < columnNames.length ; b++){
                        data[a][b] = DataSiswa.database.get(a).getData().getOrDefault((columnNames[b].equals("pass"))?"passhash":columnNames[b], null);
                    }
                }

                dm.setDataVector(data, columnNames);
                table.setModel(dm);
            }
        });
    }

    public Rapor(){
        super("Database Rapor Siswa");

        dm.setDataVector(null, columnNames);
        table.setModel(dm);
        kelasField.setModel( new DefaultComboBoxModel<DataSiswa.Kelas>(DataSiswa.Kelas.values()));
        for(int a = 1; a<=DataSiswa.maximumSemester ; a++) semesterField.addItem(a);

        createCheckListener(namaField, namaLabel, namaCheck);
        createCheckListener(kelasField, kelasLabel, kelasCheck);
        createCheckListener(semesterField, semesterLabel, semesterCheck);
        createCheckListener(rombelField, rombelLabel, rombelCheck);
        createCheckListener(newEmailField, newEmailLabel, newEmailCheck);
        createCheckListener(newPasswordField, newPasswordLabel, newPasswordCheck);
        createCheckListener(commentField, commentLabel, commentCheck);

        createRadioListener(addRadioButton, OperationState.ADD);
        createRadioListener(updateRadioButton, OperationState.UPDATE);
        createRadioListener(deleteRadioButton, OperationState.DELETE);

        createKelasListener();

        createSubmitListener();

        createValidatorListener();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
    }

    private static enum OperationState {
        ADD, UPDATE, DELETE
    }

    public static void main(String[] args) {
        DataSiswa.Kelas.MIA.setRombel(7);
        DataSiswa.Kelas.IIS.setRombel(5);
        DataSiswa.Kelas.BHS.setRombel(3);

        JFrame frame = new Rapor();
        frame.setVisible(true);
    }


}
