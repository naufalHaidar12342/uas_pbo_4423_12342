import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.sql.*;

public class EditData extends PenyewaanBuku{
    public JPanel halamanEdit;
    private JLabel editData;
    private JLabel ubahJudul;
    private JPanel panelUser;
    private JTextField fieldJudul;
    private JTextField fieldPinjam;
    private JTextField fieldWajibKembali;
    private JPanel tableEdit;
    private JTable tableDanAksi;
    private JLabel labelPinjam;
    private JLabel wajibKembali;

    private JButton tombolUpdate;


    private JButton tombolDelete;

    static final String DriverDB;
    static final String URL;
    static final String USERNAME;
    static final String PASSWORD;

    static Connection connectDB;
    static Statement statmt;
    static ResultSet setHasil;
    static {
        DriverDB= JDBC_DRIVER;
        URL=DB_URL;
        USERNAME=USER;
        PASSWORD=PASS;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("EditData");
        frame.setContentPane(new EditData().halamanEdit);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(1024,720);
    }



    public EditData() {
        showEdit();
    }



    public void showEdit() {
        //super.show();
        //mengatur penempatan teks/teks alignment
        DefaultTableCellRenderer alignCenter= new DefaultTableCellRenderer();
        alignCenter.setHorizontalAlignment(JLabel.CENTER);

        try {
            //register driver yang akan dipakai
            Class.forName(DriverDB);

            //menyambungkan ke database
            connectDB = DriverManager.getConnection(URL,USERNAME,PASSWORD);

            //mengatur table head atau nama kolom
            DefaultTableModel kerangkaTabel = new DefaultTableModel();
            kerangkaTabel.addColumn("Nomer");
            kerangkaTabel.addColumn("Judul Buku");
            kerangkaTabel.addColumn("Tanggal Pinjam");
            kerangkaTabel.addColumn("Tanggal Harus Kembali");
            kerangkaTabel.addColumn("Tanggal Kembali");
            kerangkaTabel.addColumn("Denda");
            kerangkaTabel.addColumn("Biaya Sewa");

            //perintah sql nya
            statmt= connectDB.createStatement();
            String sql = "SELECT * FROM sewabuku";

            //eksekusi perintah sql
            setHasil= statmt.executeQuery(sql);

            //nomor urut untuk di dalam tabel
            //supaya tidak menggunakan id
            int no_urut=1;
            while (setHasil.next()){
                kerangkaTabel.addRow(new Object[] {
                        //setHasil.getString("id"),
                        no_urut,
                        setHasil.getString("judul"),
                        setHasil.getString("tanggal_pinjam"),
                        setHasil.getString("tanggal_harus_kembali"),
                        setHasil.getString("tanggal_kembali"),
                        setHasil.getString("denda"),
                        setHasil.getString("biaya_sewa")
                });
                no_urut++;

            }
            setHasil.close();
            connectDB.close();
            statmt.close();

            //set table model tadi ke dalam JTables
            tableDanAksi.setModel(kerangkaTabel);

            //mengatur tinggi tiap baris
            tableDanAksi.setRowHeight(30);


            //mengatur penempatan teks/teks alignment tiap kolom, index kolom dimulai dari 0

            /*indeks 0 : kolom nomer
             * indeks 1 : kolom nama buku
             * indeks 2 : kolom tanggal pinjam
             * indeks 3 : kolom tanggal harus kembali
             * indeks 4 : kolom tanggal kembali
             * indeks 5 : kolom denda
             * indeks 6 : kolom sewa*/
            tableDanAksi.getColumnModel().getColumn(0).setCellRenderer(alignCenter);
            tableDanAksi.getColumnModel().getColumn(2).setCellRenderer(alignCenter);
            tableDanAksi.getColumnModel().getColumn(3).setCellRenderer(alignCenter);
            tableDanAksi.getColumnModel().getColumn(4).setCellRenderer(alignCenter);

            //mengatur lebar kolom
            TableColumnModel setKolom = tableDanAksi.getColumnModel();
            setKolom.getColumn(0).setPreferredWidth(5);
//            setKolom.getColumn(0).setPreferredWidth(5);
//            setKolom.getColumn(0).setPreferredWidth(5);
//            setKolom.getColumn(0).setPreferredWidth(5);
//            setKolom.getColumn(0).setPreferredWidth(5);
//            setKolom.getColumn(0).setPreferredWidth(5);


        }catch (SQLException eksepsi){
            System.out.println(eksepsi.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
