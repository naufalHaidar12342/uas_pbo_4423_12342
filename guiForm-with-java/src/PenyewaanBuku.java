import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    protected JTable tableDanAksi;
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
    private JScrollBar scrollBar1;
    private JLabel fieldID;

    //kebutuhan untuk menyambungkan database ke gui form kita
    protected static final String JDBC_DRIVER;
    protected static final String DB_URL;
    protected static final String USER;
    protected static final String PASS;

    static {
        JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
        DB_URL = "jdbc:mysql://localhost/perpus_sekolah";
        USER = "root";
        PASS = "";
    }



    protected static Connection connectDB;
    protected static Statement statmt;
    protected static ResultSet setHasil;

    protected static final int biayaSewa=5000;

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

    protected PenyewaanBuku() {

        //menyembunyikan fieldID
        //fieldID.setVisible(false);

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
//        tableBuku.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                super.mouseClicked(e);
//                int barisDipilih=tableBuku.getSelectedRow();
//                if (barisDipilih!=-1){
//                    //jika barisDipilih valuenya bukan -1 (tidak ada baris yang dipilih)
//                    //gunakan mouseListener milik buttonEdit
//
//                    //buttonEdit akan mengambil value dari barisDipilih
//                    buttonEdit.addMouseListener(new MouseAdapter() {
//                        @Override
//                        public void mouseClicked(MouseEvent e) {
//                            //super.mouseClicked(e);
//                            fieldBuku.setText(tableBuku.getValueAt(barisDipilih,1).toString());
//
//                        }
//                    });
//                }
//
//
//            }
//        });




        buttonEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }
    //menghitung perbedaan hari dari dua tanggal
    public long selisihHari(LocalDate harusKembali, LocalDate waktuKembaliAslinya){
        try {
            //register driver yang akan dipakai
            Class.forName(JDBC_DRIVER);

            //menyambungkan ke database
            connectDB = DriverManager.getConnection(DB_URL,USER,PASS);

            //perintah sql untuk mengambil tanggal harus kembali
            String sql = "SELECT tanggal_harus_kembali FROM sewabuku";

            PreparedStatement prstmt= connectDB.prepareStatement(sql);

        }catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }


        long bedaHari=1-2;

        return Math.abs(bedaHari);
    }




    //insert ketika tombol 'Simpan' di-klik
    public void insert(String judulBuku, LocalDate tanggalPinjam, LocalDate wajibKembali){
        try {
            //register driver yang akan dipakai
            Class.forName(JDBC_DRIVER);

            //menyambungkan ke database
            connectDB = DriverManager.getConnection(DB_URL,USER,PASS);

            //perintah sql-nya
            String sql="INSERT INTO sewabuku (judul, tanggal_pinjam, tanggal_harus_kembali) VALUES (?, ?, ?)";

            //Prepared statement untuk menghindari sql injection
            PreparedStatement prstmt= connectDB.prepareStatement(sql);
            prstmt.setString(1, judulBuku);
            prstmt.setString(2, String.valueOf(tanggalPinjam));
            prstmt.setString(3, String.valueOf(wajibKembali));

            prstmt.execute();

            //tutup koneksi
            connectDB.close();


        }catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    //menampilkan isi dari tabel pada database
    public void show(){
        //mengatur penempatan teks/teks alignment
        DefaultTableCellRenderer alignCenter= new DefaultTableCellRenderer();
        DefaultTableCellRenderer alignJudul= new DefaultTableCellRenderer();
        alignCenter.setHorizontalAlignment(JLabel.CENTER);
        alignJudul.setHorizontalAlignment(JLabel.LEADING);

        try {
            //register driver yang akan dipakai
            Class.forName(JDBC_DRIVER);

            //menyambungkan ke database
            connectDB = DriverManager.getConnection(DB_URL,USER,PASS);

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
            tableDanAksi.setRowHeight(100);
            tableDanAksi.setRowMargin(40);
            tableDanAksi.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);


            //mengatur penempatan teks/teks alignment tiap kolom, index kolom dimulai dari 0

            /*indeks 0 : kolom nomer
            * indeks 1 : kolom nama buku
            * indeks 2 : kolom tanggal pinjam
            * indeks 3 : kolom tanggal harus kembali
            * indeks 4 : kolom tanggal kembali
            * indeks 5 : kolom denda
            * indeks 6 : kolom sewa*/
            tableDanAksi.getColumnModel().getColumn(0).setCellRenderer(alignCenter);
            tableDanAksi.getColumnModel().getColumn(1).setCellRenderer(alignJudul);
            tableDanAksi.getColumnModel().getColumn(2).setCellRenderer(alignCenter);
            tableDanAksi.getColumnModel().getColumn(3).setCellRenderer(alignCenter);
            tableDanAksi.getColumnModel().getColumn(4).setCellRenderer(alignCenter);

            //mengatur lebar kolom
            TableColumnModel setKolom = tableDanAksi.getColumnModel();
            setKolom.getColumn(0).setPreferredWidth(4);
            setKolom.getColumn(1).setPreferredWidth(30);
            setKolom.getColumn(2).setPreferredWidth(10);
            setKolom.getColumn(3).setPreferredWidth(10);
            setKolom.getColumn(4).setPreferredWidth(10);
            setKolom.getColumn(5).setPreferredWidth(10);
            setKolom.getColumn(6).setPreferredWidth(10);


        }catch (SQLException eksepsi){
            System.out.println(eksepsi.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //update ketika tombol 'btnKembaliBuku' di-klik
    public void update(LocalDate tanggalKembali, int denda, int biayaSewa, int id){
        try {
            //register driver yang akan dipakai
            Class.forName(JDBC_DRIVER);

            //menyambungkan ke database
            connectDB = DriverManager.getConnection(DB_URL,USER,PASS);

            //perintah sqlnya untuk update table
            String sql="UPDATE sewabuku SET tanggal_kembali=? ,denda=? , biaya_sewa=? WHERE id=?";

            //prepared statement untuk update
            PreparedStatement prstmt= connectDB.prepareStatement(sql);
            prstmt.setString(1, String.valueOf(tanggalKembali));
            prstmt.setString(2, String.valueOf(denda));
            prstmt.setString(3, String.valueOf(biayaSewa));
            prstmt.setString(4, String.valueOf(id));

            prstmt.executeUpdate();

            prstmt.close();
            connectDB.close();



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
            connectDB = DriverManager.getConnection(DB_URL,USER,PASS);

            //perintah sql
            String sql="DELETE FROM sewabuku ORDER BY id DESC LIMIT 1 ";
            statmt= connectDB.createStatement();
            statmt.executeUpdate(sql);

            statmt.close();
            connectDB.close();



        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }
}
