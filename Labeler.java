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
    private JButton T2Button;
    private JButton F2Button;
    private JButton NButton;
    private JButton playButton;
    private JLabel fileName;

    private File[] pathList;
    private int fileCnt;
    private String outputPath;
    private File logFile;

    private int labelNum;
    private boolean isInput;
    private boolean isPlaying;
    private boolean isFinish;

    private Thread playThread;

    private AudioClip audio = null;

    Labeler(String title, File[] pathL, String output){
        this.pathList = pathL;
        this.outputPath = output;
        this.fileCnt = 1;
        this.isInput = true;
        this.isPlaying = false;
        this.isFinish = false;
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
        this.setBounds(100, 100, 880, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //close時の時の処理?

        JPanel msg_box = new JPanel();
        this.fileName = new JLabel();
        this.fileName.setFont(new Font("Century", Font.ITALIC, 40));
        this.fileName.setText(String.valueOf(this.pathList[this.fileCnt]).replace("databox\\", ""));
        msg_box.add(this.fileName);

        JPanel playPan = new JPanel();
        ImageIcon iPlay = new ImageIcon("img/play.png");
        this.playButton = new JButton(iPlay);
        playPan.add(this.playButton);
        this.playButton.addActionListener(this);


        JPanel evalPan = new JPanel();

        ImageIcon iMinus = new ImageIcon("img/minus.png");
        ImageIcon iPlus = new ImageIcon("img/plus.png");
        ImageIcon iMinus2 = new ImageIcon("img/minus2.png");
        ImageIcon iPlus2 = new ImageIcon("img/plus2.png");
        ImageIcon iZero = new ImageIcon("img/0.png");


        this.FButton = new JButton(iMinus);
        this.TButton = new JButton(iPlus);
        this.NButton = new JButton(iZero);
        this.F2Button = new JButton(iMinus2);
        this.T2Button = new JButton(iPlus2);

        evalPan.add(this.F2Button);
        evalPan.add(this.FButton);
        evalPan.add(this.NButton);
        evalPan.add(this.TButton);
        evalPan.add(this.T2Button);

        this.T2Button.addActionListener(this);
        this.TButton.addActionListener(this);
        this.NButton.addActionListener(this);
        this.FButton.addActionListener(this);
        this.F2Button.addActionListener(this);

        this.getContentPane().add(msg_box, BorderLayout.NORTH);
        this.getContentPane().add(playPan, BorderLayout.CENTER);
        this.getContentPane().add(evalPan, BorderLayout.SOUTH);

        //this.playThread.start();
    }

    private void iconReset(){
        ImageIcon iMinus = new ImageIcon("img/minus.png");
        ImageIcon iPlus = new ImageIcon("img/plus.png");
        ImageIcon iMinus2 = new ImageIcon("img/minus2.png");
        ImageIcon iPlus2 = new ImageIcon("img/plus2.png");
        ImageIcon iZero = new ImageIcon("img/0.png");

        this.TButton.setIcon(iPlus);
        this.FButton.setIcon(iMinus);
        this.T2Button.setIcon(iPlus2);
        this.F2Button.setIcon(iMinus2);
        this.NButton.setIcon(iZero);
    }

    public void actionPerformed(ActionEvent e){
        Object obj = e.getSource();
        if(this.isFinish){
            System.exit(0);
        }
        if(obj == this.playButton){
            this.playThread.start();
            this.isInput = true;
        }
        else if(obj == this.TButton){
            if(isInput){
                if(isPlaying){
                    this.iconReset();
                    ImageIcon iPlusC = new ImageIcon("img/plus_clear.png");
                    this.TButton.setIcon(iPlusC);
                    this.labelNum = 1;
                }else{
                    this.send(this.outputPath, this.pathList[this.fileCnt], 1);
                    if(!this.isFinish){
                        this.fileName.setText(String.valueOf(this.pathList[this.fileCnt]).replace("databox\\", ""));
                        this.playThread.start();
                    }
                }
            }
        }else if(obj == this.FButton){
            if(isInput){
                if(isPlaying){
                    this.iconReset();
                    ImageIcon iMinusC = new ImageIcon("img/minus_clear.png");
                    this.FButton.setIcon(iMinusC);
                    this.labelNum = -1;
                }else{
                    this.send(this.outputPath, this.pathList[this.fileCnt], -1);
                    if(!this.isFinish){
                        this.fileName.setText(String.valueOf(this.pathList[this.fileCnt]).replace("databox\\", ""));
                        this.playThread.start();
                    }
                }
            }
        }else if(obj == this.T2Button){
            if(isInput){
                if(isPlaying){
                    this.iconReset();
                    ImageIcon iPlusC = new ImageIcon("img/plus2_clear.png");
                    this.T2Button.setIcon(iPlusC);
                    this.labelNum = 2;
                }else{
                    this.send(this.outputPath, this.pathList[this.fileCnt], 2);
                    if(!this.isFinish){
                        this.fileName.setText(String.valueOf(this.pathList[this.fileCnt]).replace("databox\\", ""));
                        this.playThread.start();
                    }
                }
            }
        }else if(obj == this.F2Button){
            if(isInput){
                if(isPlaying){
                    this.iconReset();
                    ImageIcon iMinusC = new ImageIcon("img/minus2_clear.png");
                    this.F2Button.setIcon(iMinusC);
                    this.labelNum = -2;
                }else{
                    this.send(this.outputPath, this.pathList[this.fileCnt], -2);
                    if(!this.isFinish){
                        this.fileName.setText(String.valueOf(this.pathList[this.fileCnt]).replace("databox\\", ""));
                        this.playThread.start();
                    }
                }
            }
        }else if(obj == this.NButton){
            if(isInput){
                if(isPlaying){
                    this.iconReset();
                    ImageIcon iZero = new ImageIcon("img/0_clear.png");
                    this.NButton.setIcon(iZero);
                    this.labelNum = 0;
                }else{
                    this.send(this.outputPath, this.pathList[this.fileCnt], 0);
                    if(!this.isFinish){
                        this.fileName.setText(String.valueOf(this.pathList[this.fileCnt]).replace("databox\\", ""));
                        this.playThread.start();
                    }
                }
            }
        }
    }

    @Override
    public void run(){
        this.isPlaying = true;
        ImageIcon iPlan = new ImageIcon("img/planaria.png");
        this.playButton.setIcon(iPlan);
        this.play(this.pathList[this.fileCnt]);
        if(this.labelNum != -1){
            this.send(this.outputPath, this.pathList[this.fileCnt], this.labelNum);
            if(!this.isFinish){
                this.fileName.setText(String.valueOf(this.pathList[this.fileCnt]).replace("databox\\", ""));
                this.labelNum = -1;
                ImageIcon iPlay = new ImageIcon("img/play.png");
                this.playButton.setIcon(iPlay);
            }
            this.isInput = false;
        }else{
            ImageIcon iAgain = new ImageIcon("img/again.png");
            this.playButton.setIcon(iAgain);
        }
        this.isPlaying = false;
        this.playThread = new Thread(this);
    }

    private void finCheck(){
        if(this.fileCnt >= this.pathList.length){
            this.fileName.setText("<html>Labeling is now complete.<br>Thank you for your cooperation!");
            this.fileName.setFont(new Font("Century", Font.ITALIC, 20));
            ImageIcon ikuma = new ImageIcon("img/kuma.png");
            ImageIcon ia = new ImageIcon("img/a.png");
            ImageIcon iri = new ImageIcon("img/ri.png");
            ImageIcon iga = new ImageIcon("img/ga.png");
            ImageIcon ito = new ImageIcon("img/to.png");
            ImageIcon iu = new ImageIcon("img/u.png");

            this.playButton.setIcon(ikuma);

            this.F2Button.setIcon(ia);
            this.FButton.setIcon(iri);
            this.NButton.setIcon(iga);
            this.TButton.setIcon(ito);
            this.T2Button.setIcon(iu);

            this.isFinish = true;
        }
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
        this.iconReset();

        this.fileCnt += 1;
        this.finCheck();
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
