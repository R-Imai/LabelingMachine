import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;

import java.lang.Runnable;

import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import javax.sound.sampled.*;



class Labeler extends JFrame implements ActionListener, Runnable{
    private JButton TButton;
    private JButton FButton;
    private JButton againButton;
    private JLabel fileName;

    private File[] pathList;
    private int fileCnt;
    private String outputPath;

    private int labelNum;
    private boolean isInput;
    private boolean isPlaying;

    private Thread playThread;

    Labeler(String title, File[] pathL, String output){
        this.pathList = pathL;
        this.outputPath = output;
        this.fileCnt = 0;
        this.isInput = true;
        this.isPlaying = false;
        this.labelNum = -1;

        this.playThread = new Thread(this);

        this.setTitle(title);
        this.setBounds(100, 100, 400, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //close時の時の処理?

        JPanel msg_box = new JPanel();
        this.fileName = new JLabel();
        this.fileName.setText(String.valueOf(this.pathList[this.fileCnt]));
        msg_box.add(this.fileName);

        JPanel pan = new JPanel();


        this.FButton = new JButton("自信なし");
        this.againButton = new JButton("もう一回");
        this.TButton = new JButton("自信あり");

        pan.add(this.FButton);
        pan.add(this.againButton);
        pan.add(this.TButton);

        this.TButton.addActionListener(this);
        this.againButton.addActionListener(this);
        this.FButton.addActionListener(this);

        this.getContentPane().add(msg_box, BorderLayout.CENTER);
        this.getContentPane().add(pan, BorderLayout.SOUTH);

        this.playThread.start();
    }

    public void actionPerformed(ActionEvent e){
        Object obj = e.getSource();
        if(obj == this.TButton){
            if(isInput){
                if(isPlaying){
                    this.labelNum = 1;
                }else{
                    this.toCsv(this.outputPath, this.pathList[this.fileCnt], 1);
                    this.fileCnt += 1;
                    this.fileName.setText(String.valueOf(this.pathList[this.fileCnt]));
                    this.playThread.start();
                }
            }
        }else if(obj == this.againButton){
            this.playThread.start();
            this.isInput = true;
        }else if(obj == this.FButton){
            if(isInput){
                if(isPlaying){
                    this.labelNum = 0;
                }else{
                    this.toCsv(this.outputPath, this.pathList[this.fileCnt], 0);
                    this.fileCnt += 1;
                    this.fileName.setText(String.valueOf(this.pathList[this.fileCnt]));
                    this.playThread.start();
                }
            }
        }
    }

    @Override
    public void run(){
        this.isPlaying = true;
        this.againButton.setText("もう一回");
        this.play(this.pathList[this.fileCnt]);
        if(this.labelNum != -1){
            this.toCsv(this.outputPath, this.pathList[this.fileCnt], this.labelNum);
            this.fileCnt += 1;
            this.fileName.setText(String.valueOf(this.pathList[this.fileCnt]));
            this.labelNum = -1;
            this.againButton.setText("再生");
            this.isInput = false;
        }
        this.isPlaying = false;
        this.playThread = new Thread(this);
    }

    private static void play(File path){
        try {
            AudioInputStream ais =
                AudioSystem.getAudioInputStream(path);
            // オーディオ入力ストリームからデータを読む
            byte [] data = new byte [ais.available()];
            ais.read(data);
            ais.close();
            // ファイルのフォーマットを調べる
            AudioFormat af = ais.getFormat();
            // 再生する
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
            SourceDataLine line = (SourceDataLine)AudioSystem.getLine(info);
            line.open(af);
            line.start();
            line.write(data, 0, data.length);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void toCsv(String path, File wavPath, int num) {
        try {
            //出力先を作成する
            FileWriter fw = new FileWriter(path, true);  //※１
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

            pw.println(wavPath + "," + String.valueOf(num));

            //ファイルに書き出す
            pw.close();

            //終了メッセージを画面に出力する
            //System.out.println("出力が完了しました。");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String args[]){
        File dir = new File(args[0]);
        File[] files = dir.listFiles();

        Labeler model = new Labeler("Labeling tools", files, "output.csv");
        model.setVisible(true);
    }
}
