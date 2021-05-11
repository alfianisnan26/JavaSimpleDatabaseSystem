import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ItemEvent;
import java.util.Arrays;

public class LaporanAdministrasi extends JFrame {
    private JTable table;
    private JRadioButton addRadioButton;
    private JRadioButton updateRadioButton;
    private JRadioButton deleteRadioButton;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField namaField;
    private JCheckBox namaCheck;
    private JCheckBox tanggalCheck;
    private JCheckBox jamMasukCheck;
    private JCheckBox jamKeluarCheck;
    private JTextField commentField;
    private JCheckBox commentCheck;
    private JPasswordField newPasswordField;
    private JCheckBox newPasswordCheck;
    private JCheckBox newEmailCheck;
    private JTextField newEmailField;
    private JButton submitButton;
    private JPanel mainPanel;
    private JLabel namaLabel;
    private JLabel tanggalLabel;
    private JLabel jamMasukLabel;
    private JLabel jamKeluarLabel;
    private JLabel commentLabel;
    private JLabel newEmailLabel;
    private JLabel newPasswordLabel;
    private JCheckBox passwordCheck;
    private JCheckBox emailCheck;
    private JPasswordField passwordConfirmField;
    private JCheckBox passwordConfirmCheck;
    private JLabel passwordConfirmLabel;
    private JTextField jamMasukField;
    private JTextField tanggalField;
    private JTextField jamKeluarField;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JScrollPane tableScrollPane;
    private JPanel operationPanel;
    private JPanel formPanel;
    private JLabel emailLabel;
    private JLabel passwordLabel;
    private JPanel submitPanel;
    private final DefaultTableModel dm = new DefaultTableModel(){
        public boolean isCellEditable(int row, int column)
        {
            return false;
        }
    };
    private final String[] columnNames = {
            "id",
            "nama",
            "tanggal_kerja",
            "jam_masuk",
            "jam_pulang",
            "email",
            "pass",
            "comment"
    };

    OperationState state = OperationState.ADD;

    @FunctionalInterface
    public interface SimpleDocumentListener extends DocumentListener {
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
        check.addItemListener(e -> {
            boolean state = (e.getStateChange() == ItemEvent.SELECTED);
            field.setEnabled(state);
            label.setEnabled(state);
        });
    }

    private void updateFormState(OperationState state){
        final boolean checkState = (state == OperationState.UPDATE);
        final boolean selected = (state == OperationState.ADD);
        setObjectEnable(namaCheck, checkState, selected);
        setObjectEnable(tanggalCheck, checkState, selected);
        setObjectEnable(jamKeluarCheck, checkState, selected);
        setObjectEnable(jamMasukCheck, checkState, selected);
        setObjectEnable(newEmailCheck, (state == OperationState.UPDATE), false);
        setObjectEnable(newPasswordCheck, (state == OperationState.UPDATE), false);
        setObjectEnable(commentCheck, (state != OperationState.DELETE), false);
        passwordField.setText("");
        passwordConfirmField.setText("");
        setObjectEnable(passwordConfirmCheck, false, false);
        passwordConfirmField.setEnabled(state == OperationState.ADD);
        passwordConfirmLabel.setEnabled(state == OperationState.ADD);
    }

    private void createRadioListener(JRadioButton radio, OperationState setState){
        radio.addActionListener(e -> {
            state = setState;
            updateFormState(state);
            System.out.println("Database State = " + state);
        });
    }

    private void createValidatorListener(){
        passwordField.getDocument().addDocumentListener((SimpleDocumentListener) e ->
            passwordCheck.setSelected(DataKaryawan.Validator.passIsValid(String.valueOf(passwordField.getPassword()))));
        passwordConfirmField.getDocument().addDocumentListener((SimpleDocumentListener) e ->
            passwordConfirmCheck.setSelected(Arrays.equals(passwordField.getPassword(), passwordConfirmField.getPassword())));
        emailField.getDocument().addDocumentListener((SimpleDocumentListener) e ->
            emailCheck.setSelected(DataKaryawan.Validator.emailIsValid(emailField.getText())));
    }

    private void createSubmitListener(){
        submitButton.addActionListener(e -> {
            switch (state) {
                case ADD -> {
                    if (!passwordConfirmCheck.isSelected()) {
                        JOptionPane.showMessageDialog(null, "Password Konfirmasi tidak sesuai, pastikan password yang anda masukkan sesuai");
                        return;
                    }
                    try {
                        DataKaryawan ds = new DataKaryawan(namaField.getText(), tanggalField.getText(), jamMasukField.getText(), jamKeluarField.getText(), emailField.getText(), passwordField.getPassword(), commentField.getText());
                        DataKaryawan.add(ds);
                    } catch (DataKaryawan.Validator.PasswordInvalidException err) {
                        JOptionPane.showMessageDialog(null, "Password tidak dapat diterima, pastikan password memiliki minimal satu huruf besar, satu huruf kecil, satu angka, dan berjumlah antara 8 sampai 20 karakter");
                    } catch (DataKaryawan.EmailAlreadyExistsException err) {
                        JOptionPane.showMessageDialog(null, "Email sudah dipakai oleh pengguna lain, mohon masukkan email lainnya");
                    } catch (DataKaryawan.Validator.EmailInvalidException err) {
                        JOptionPane.showMessageDialog(null, "Email tidak dapat diterima, pastikan email sesuai dengan kriteria");
                    } catch (NullPointerException | DataKaryawan.DataCannotNullException err) {
                        JOptionPane.showMessageDialog(null, "Pastikan anda mengisi atau memilih entri data dengan benar | " + err.getMessage());
                    } catch (DataKaryawan.Validator.TimeValueInvalidException timeValueInvalidException) {
                        JOptionPane.showMessageDialog(null, "Format Waktu Salah! mohon masukkan waktu dengan format hh:mm:ss");
                    } catch (DataKaryawan.Validator.DateValueInvalidException dateValueInvalidException) {
                        JOptionPane.showMessageDialog(null, "Format Tanggal Salah! mohon masukkan tanggal dengan format dd/mm/yyyy dengan awalan nol untuk satuan");
                    } catch (Exception err) {
                        JOptionPane.showMessageDialog(null, "Error Tidak Dikenal : " + err.getMessage());
                    }
                }
                case UPDATE -> {
                    String newNama = null;
                    String newTanggal = null;
                    String newJamMasuk = null;
                    String newJamKeluar = null;
                    String newComment = null;
                    String newEmail = null;
                    char[] newPassword = null;
                    if (namaCheck.isSelected()) newNama = namaField.getText();
                    if (tanggalCheck.isSelected()) newTanggal = tanggalField.getText();
                    if (jamMasukCheck.isSelected()) newJamMasuk = jamMasukField.getText();
                    if (jamKeluarCheck.isSelected()) newJamKeluar = jamKeluarField.getText();
                    if (commentCheck.isSelected()) newComment = commentField.getText();
                    if (newEmailCheck.isSelected()) newEmail = newEmailField.getText();
                    if (newPasswordCheck.isSelected()) newPassword = newPasswordField.getPassword();
                    try {
                        DataKaryawan.updateByUser(emailField.getText(), passwordField.getPassword(), newNama, newTanggal, newJamMasuk, newJamKeluar, newComment, newEmail, newPassword);
                    } catch (DataKaryawan.UserNotFoundException userNotFoundException) {
                        JOptionPane.showMessageDialog(null, "Pengguna tidak ditemukan atau Password salah, mohon cek kembali");
                    } catch (DataKaryawan.EmailAlreadyExistsException emailAlreadyExistsException) {
                        JOptionPane.showMessageDialog(null, "Email sudah dipakai oleh pengguna lain, mohon masukkan email lainnya");
                    } catch (DataKaryawan.Validator.EmailInvalidException emailInvalidException) {
                        JOptionPane.showMessageDialog(null, "Email tidak dapat diterima, pastikan email sesuai dengan kriteria");
                    } catch (DataKaryawan.Validator.PasswordInvalidException passwordInvalidException) {
                        JOptionPane.showMessageDialog(null, "Password tidak dapat diterima, pastikan password memiliki minimal satu huruf besar, satu huruf kecil, satu angka, dan berjumlah antara 8 sampai 20 karakter");
                    } catch (DataKaryawan.DataCannotNullException dataCannotNullException) {
                        JOptionPane.showMessageDialog(null, "Pastikan anda mengisi atau memilih entri data dengan benar | " + dataCannotNullException.getMessage());
                    } catch (DataKaryawan.Validator.TimeValueInvalidException timeValueInvalidException) {
                        JOptionPane.showMessageDialog(null, "Format Waktu Salah! mohon masukkan waktu dengan format hh:mm:ss");
                    } catch (DataKaryawan.Validator.DateValueInvalidException dateValueInvalidException) {
                        JOptionPane.showMessageDialog(null, "Format Tanggal Salah! mohon masukkan tanggal dengan format dd/mm/yyyy dengan awalan nol untuk satuan");
                    } catch (Exception err) {
                        JOptionPane.showMessageDialog(null, "Error Tidak Dikenal : " + err.getMessage());
                    }
                }
                case DELETE -> {
                    try {
                        DataKaryawan.deleteByUser(emailField.getText(), passwordField.getPassword());
                    } catch (DataKaryawan.UserNotFoundException userNotFoundException) {
                        JOptionPane.showMessageDialog(null, "Pengguna tidak ditemukan atau Password salah, mohon cek kembali");
                    } catch (Exception err) {
                        JOptionPane.showMessageDialog(null, "Error Tidak Dikenal : " + err.getMessage());
                    }
                }
            }
            Object[][] data = new Object[DataKaryawan.database.size()][columnNames.length];

            for(int a = 0; a < DataKaryawan.database.size() ; a++){
                for(int b = 0; b < columnNames.length ; b++){
                    data[a][b] = DataKaryawan.database.get(a).getData().getOrDefault((columnNames[b].equals("pass"))?"passhash":columnNames[b], null);
                }
            }

            dm.setDataVector(data, columnNames);
            table.setModel(dm);
        });
    }

    public LaporanAdministrasi(){
        super("Database Laporan Administrasi Karyawan");
        dm.setDataVector(null, columnNames);
        table.setModel(dm);

        createCheckListener(namaField, namaLabel, namaCheck);
        createCheckListener(tanggalField, tanggalLabel, tanggalCheck);
        createCheckListener(jamKeluarField, jamKeluarLabel, jamKeluarCheck);
        createCheckListener(jamMasukField, jamMasukLabel, jamMasukCheck);
        createCheckListener(newEmailField, newEmailLabel, newEmailCheck);
        createCheckListener(newPasswordField, newPasswordLabel, newPasswordCheck);
        createCheckListener(commentField, commentLabel, commentCheck);

        createRadioListener(addRadioButton, OperationState.ADD);
        createRadioListener(updateRadioButton, OperationState.UPDATE);
        createRadioListener(deleteRadioButton, OperationState.DELETE);

        createSubmitListener();

        createValidatorListener();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
    }

    private enum OperationState {
        ADD, UPDATE, DELETE
    }

    public static void main(String[] args) {
        JFrame frame = new LaporanAdministrasi();
        frame.setVisible(true);
    }


}
