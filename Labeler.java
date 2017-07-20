import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;

import java.lang.Runnable;

import java.awt.Font;

import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.lang.Runtime;

import java.applet.AudioClip;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;

import javax.sound.sampled.*;





class Labeler extends JFrame implements ActionListener, Runnable{
    private JButton TButton;
    private JButton FButton;
    private JButton againButton;
    private JLabel fileName;

    private File[] pathList;
    private int fileCnt;
    private String outputPath;
    private File logFile;

    private int labelNum;
    private boolean isInput;
    private boolean isPlaying;

    private Thread playThread;

    private AudioClip audio = null;

    Labeler(String title, File[] pathL, String output){
        this.pathList = pathL;
        this.outputPath = output;
        this.fileCnt = 1;
        this.isInput = true;
        this.isPlaying = false;
        this.labelNum = -1;
        this.logFile = new File("player.log");

        if(this.logFile.exists()){
            try{
                FileReader fr = new FileReader(this.logFile);
                BufferedReader br = new BufferedReader(fr);
                this.fileCnt = Integer.parseInt(br.readLine());
                br.close();
            }catch(FileNotFoundException e){
                System.out.println(e);
            }catch(IOException e){
                System.out.println(e);
            }
        }

        this.playThread = new Thread(this);

        this.setTitle(title);
        this.setBounds(100, 100, 550, 250);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //close時の時の処理?

        JPanel msg_box = new JPanel();
        this.fileName = new JLabel();
        this.fileName.setFont(new Font("Century", Font.ITALIC, 40));
        this.fileName.setText(String.valueOf(this.pathList[this.fileCnt]).replace("databox\\", ""));
        msg_box.add(this.fileName);

        JPanel pan = new JPanel();

        ImageIcon iMinus = new ImageIcon("img/minus.png");
        ImageIcon iAgain = new ImageIcon("img/again.png");
        ImageIcon iPlus = new ImageIcon("img/plus.png");
        ImageIcon iPlay = new ImageIcon("./img/play.png");

        this.FButton = new JButton(iMinus);
        this.againButton = new JButton(iPlay);
        this.TButton = new JButton(iPlus);

        pan.add(this.FButton);
        pan.add(this.againButton);
        pan.add(this.TButton);

        this.TButton.addActionListener(this);
        this.againButton.addActionListener(this);
        this.FButton.addActionListener(this);

        this.getContentPane().add(msg_box, BorderLayout.CENTER);
        this.getContentPane().add(pan, BorderLayout.SOUTH);

        //this.playThread.start();
    }

    public void actionPerformed(ActionEvent e){
        Object obj = e.getSource();
        if(obj == this.TButton){
            if(isInput){
                if(isPlaying){
                    ImageIcon iMinus = new ImageIcon("img/minus.png");
                    this.FButton.setIcon(iMinus);
                    ImageIcon iPlusC = new ImageIcon("img/plus_clear.png");
                    this.TButton.setIcon(iPlusC);
                    this.labelNum = 1;
                }else{
                    this.send(this.outputPath, this.pathList[this.fileCnt], 1);
                    this.fileCnt += 1;
                    this.fileName.setText(String.valueOf(this.pathList[this.fileCnt]).replace("databox\\", ""));
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
                    ImageIcon iPlus = new ImageIcon("img/plus.png");
                    this.TButton.setIcon(iPlus);
                    ImageIcon iMinusC = new ImageIcon("img/minus_clear.png");
                    this.FButton.setIcon(iMinusC);

                }else{
                    this.send(this.outputPath, this.pathList[this.fileCnt], 0);
                    this.fileCnt += 1;
                    this.fileName.setText(String.valueOf(this.pathList[this.fileCnt]).replace("databox\\", ""));
                    this.playThread.start();
                }
            }
        }
    }

    @Override
    public void run(){
        this.isPlaying = true;
        ImageIcon iPlan = new ImageIcon("img/planaria.png");
        this.againButton.setIcon(iPlan);
        this.play(this.pathList[this.fileCnt]);
        if(this.labelNum != -1){
            this.send(this.outputPath, this.pathList[this.fileCnt], this.labelNum);
            this.fileCnt += 1;
            this.fileName.setText(String.valueOf(this.pathList[this.fileCnt]).replace("databox\\", ""));
            this.labelNum = -1;
            ImageIcon iPlay = new ImageIcon("./img/play.png");
            this.againButton.setIcon(iPlay);
            this.isInput = false;
        }else{
            ImageIcon iAgain = new ImageIcon("img/again.png");
            this.againButton.setIcon(iAgain);
        }
        this.isPlaying = false;
        this.playThread = new Thread(this);
    }

    private void play2(File path){
        audio = java.applet.Applet.newAudioClip(getClass().getResource(path.getPath()));
		audio.play();
    }

    private void play(File path){
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(path);
            // オーディオ入力ストリームからデータを読む
            byte [] data = new byte [ais.available()];
            ais.read(data);
            // ファイルのフォーマットを調べる
            AudioFormat af = ais.getFormat();
            ais.close();
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
    private void send(String csvPath, File wavPath, int num){
        this.toCsv(csvPath, wavPath, num);
        this.writeLog();
        ImageIcon iPlus = new ImageIcon("img/plus.png");
        this.TButton.setIcon(iPlus);
        ImageIcon iMinus = new ImageIcon("img/minus.png");
        this.FButton.setIcon(iMinus);
        // File mvPath = new File(wavPath.getPath().replace("databox", "finish"));
        // this.mvFile(wavPath, mvPath);
    }

    private void toCsv(String path, File wavPath, int num) {
        try {
            //出力先を作成する
            FileWriter fw = new FileWriter(path, true);
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

            pw.println(wavPath + "," + String.valueOf(num));

            //ファイルに書き出す
            pw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void writeLog(){
        try {
            //出力先を作成する
            FileWriter fw = new FileWriter(this.logFile, false);
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

            pw.println(String.valueOf(this.fileCnt + 1));
            pw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void mvFile(File origPath, File mvPath){
        try {
            if (origPath.renameTo(mvPath)) {
            } else {
                System.out.println("failed");
            }
        } catch (SecurityException e) {
            System.out.println("Security exception");
            System.out.println(e);
        } catch (NullPointerException e) {
            System.out.println("NullPointer exception");
            System.out.println(e);
        }
    }

    public static void main(String args[]){
        File dir = new File("databox");
        File[] files = dir.listFiles();

        Labeler model = new Labeler("Labeling tools", files, "output.csv");
        model.setVisible(true);
    }
}
