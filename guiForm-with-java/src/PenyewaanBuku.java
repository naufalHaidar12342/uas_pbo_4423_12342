import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.LocalDate;

public class PenyewaanBuku {
    private JPanel panelUtama;
    private JLabel judulForm;
    private JPanel hariTanggalPanel;
    private JPanel judulBiayaPanel;
    private JPanel buttonPanel;
    private JPanel tableBukuPanel;
    private JTable tableBuku;
    private JTextField fieldTanggal;
    private JTextField fieldHari;
    private JLabel labelTanggal;
    private JLabel labelHari;
    private JTextField fieldBuku;
    private JTextField fieldRupiah;
    private JLabel judulBuku;
    private JLabel labelBiaya;
    private JLabel labelRupiah;
    private JButton buttonSimpan;
    private JButton buttonKembali;
    private JButton buttonEdit;
    private JButton buttonDelete;
    private JLabel fieldID;

    //kebutuhan untuk menyambungkan database ke gui form kita
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/perpus_sekolah";
    static final String USER="root";
    static final String PASS="";

    static Connection sambungkan;
    static Statement statmt;
    static ResultSet setHasil;

    static final int biayaSewa=5000;

    //Memanggil method LocalDate untuk mengambil tanggal dari sistem komputer kita
    //lalu disimpan dalam variabel 'tanggalTerkini'
    LocalDate tanggalTerkini=LocalDate.now();

    //mengambil hari dari LocalDate, tapi dihasilkan dari parsing 'tanggalTerkini'
    LocalDate hariTerkini=LocalDate.parse(String.valueOf(tanggalTerkini));


    public static void main(String[] args) {
        JFrame frame = new JFrame("PenyewaanBuku");
        frame.setContentPane(new PenyewaanBuku().panelUtama);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(1024,720);
    }

    public PenyewaanBuku() {
        //menyembunyikan fieldID
        fieldID.setVisible(false);

        //membuat variabel 'tampilkanTanggal' untuk menampung
        //hasil konversi string dari tanggalTerkini
        String tampilkanTanggal=String.valueOf(tanggalTerkini);

        //set value milik textfield 'fieldTanggal'
        //menjadi berisi tanggalTerkini
        fieldTanggal.setText(tampilkanTanggal);

        //membuat variabel 'tampilkanHari' untuk menampung
        //hasil konversi string dari hariTerkini
        String tampilkanHari=String.valueOf(hariTerkini);

        //set value milik textfield 'fieldHari'
        //menjadi berisi hariTerkini
        fieldHari.setText(tampilkanHari);

        //set value milik textfield 'fieldRupiah'
        fieldRupiah.setText("5.000");


        //menampilkan tabel dari database
        show();

        //ketika tombol simpan di-klik
        buttonSimpan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String tampungJudul= fieldBuku.getText();
                fieldBuku.setText("");

                //tanggap pinjam diambil dari tanggalTerkini
                LocalDate tampungPinjam=tanggalTerkini;

                //tanggal harus kembali , dimana maksimal peminjaman buku
                //adalah 7 hari sejak pinjam
                LocalDate tampungHarusKembali=tanggalTerkini.plusDays(7);

                //memasukkan ke dalam tabel kemudian ditampilkan data yang
                //baru saja dimasukkan
                insert(tampungJudul,tampungPinjam,tampungHarusKembali);
                show();
            }
        });

        //aksi yang akan dijalankan
        // ketika tombol 'buttonKembali' di klik
        // (buttonKembali digunakan untuk mengembalikan buku)
        buttonKembali.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);


            }
        });



        //aksi yang akan dijalankan
        // ketika tombol 'buttonDelete' di klik
        buttonDelete.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                deleteLastRecord();
                show();
            }
        });
        //aksi yang akan dijalankan ketika tabel di klik
        tableBuku.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int barisDipilih=tableBuku.getSelectedRow();
                if (barisDipilih!=-1){
                    //jika barisDipilih valuenya bukan -1 (tidak ada baris yang dipilih)
                    //gunakan mouseListener milik buttonEdit

                    //buttonEdit akan mengambil value dari barisDipilih
                    buttonEdit.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            //super.mouseClicked(e);
                            fieldBuku.setText(tableBuku.getValueAt(barisDipilih,1).toString());

                        }
                    });
                }


            }
        });
    }
    //menghitung perbedaan hari dari dua tanggal
    public long selisihHari(LocalDate harusKembali, LocalDate waktuKembaliAslinya){
        try {
            //register driver yang akan dipakai
            Class.forName(JDBC_DRIVER);

            //menyambungkan ke database
            sambungkan= DriverManager.getConnection(DB_URL,USER,PASS);

            //perintah sql untuk mengambil tanggal harus kembali
            String sql = "SELECT tanggal_harus_kembali FROM sewabuku";

            PreparedStatement prstmt=sambungkan.prepareStatement(sql);

        }catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }


        long bedaHari=1-2;

        return Math.abs(bedaHari);
    }




    //insert ketika tombol 'Simpan' di-klik
    public void insert(String judulBuku, LocalDate tanggalPinjam, LocalDate wajibKembali ){
        try {
            //register driver yang akan dipakai
            Class.forName(JDBC_DRIVER);

            //menyambungkan ke database
            sambungkan= DriverManager.getConnection(DB_URL,USER,PASS);

            //perintah sql-nya
            String sql="INSERT INTO sewabuku (judul, tanggal_pinjam, tanggal_harus_kembali) VALUES (?, ?, ?)";

            //Prepared statement untuk menghindari sql injection
            PreparedStatement prstmt= sambungkan.prepareStatement(sql);
            prstmt.setString(1, judulBuku);
            prstmt.setString(2, String.valueOf(tanggalPinjam));
            prstmt.setString(3, String.valueOf(wajibKembali));

            prstmt.execute();

            //tutup koneksi
            sambungkan.close();


        }catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    //menampilkan isi dari tabel pada database
    public void show(){
        try {
            //register driver yang akan dipakai
            Class.forName(JDBC_DRIVER);

            //menyambungkan ke database
            sambungkan= DriverManager.getConnection(DB_URL,USER,PASS);

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
            statmt=sambungkan.createStatement();
            String sql = "SELECT * FROM sewabuku";

            //eksekusi perintah sql
            setHasil= statmt.executeQuery(sql);

            while (setHasil.next()){
                kerangkaTabel.addRow(new Object[] {
                        setHasil.getString("id"),
                        setHasil.getString("judul"),
                        setHasil.getString("tanggal_pinjam"),
                        setHasil.getString("tanggal_harus_kembali"),
                        setHasil.getString("tanggal_kembali"),
                        setHasil.getString("denda"),
                        setHasil.getString("biaya_sewa")
                });

            }
            setHasil.close();
            sambungkan.close();
            statmt.close();

            //set table model tadi ke dalam JTables
            tableBuku.setModel(kerangkaTabel);
        }catch (SQLException eksepsi){
            System.out.println(eksepsi.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //update ketika tombol 'btnKembaliBuku' di-klik
    public void update(LocalDate tanggalKembali, int denda,int biayaSewa, int id){
        try {
            //register driver yang akan dipakai
            Class.forName(JDBC_DRIVER);

            //menyambungkan ke database
            sambungkan= DriverManager.getConnection(DB_URL,USER,PASS);

            //perintah sqlnya untuk update table
            String sql="UPDATE sewabuku SET tanggal_kembali=? ,denda=? , biaya_sewa=? WHERE id=?";

            //prepared statement untuk update
            PreparedStatement prstmt=sambungkan.prepareStatement(sql);
            prstmt.setString(1, String.valueOf(tanggalKembali));
            prstmt.setString(2, String.valueOf(denda));
            prstmt.setString(3, String.valueOf(biayaSewa));
            prstmt.setString(4, String.valueOf(id));

            prstmt.executeUpdate();

            prstmt.close();
            sambungkan.close();



        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    //delete record terakhir yang dimasukkan, berdasarkan id
    public void deleteLastRecord(){
        try {
            //register driver yang akan dipakai
            Class.forName(JDBC_DRIVER);

            //menyambungkan ke database
            sambungkan= DriverManager.getConnection(DB_URL,USER,PASS);

            //perintah sql
            String sql="DELETE FROM sewabuku ORDER BY id DESC LIMIT 1 ";
            statmt=sambungkan.createStatement();
            statmt.executeUpdate(sql);

            statmt.close();
            sambungkan.close();



        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }
}
