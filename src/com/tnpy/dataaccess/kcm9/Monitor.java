package com.tnpy.dataaccess.kcm9;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;

/**
 * 主程序
 */
public class Monitor extends Frame {

	private static final long serialVersionUID = 1L;

	/**
	 * 程序界面宽度
	 */
	public static final int WIDTH = 1600;

	/**
	 * 程序界面高度
	 */
	public static final int HEIGHT = 900;

	/**
	 * 程序界面出现位置（横坐标）
	 */
	public static final int LOC_X = 150;

	/**
	 * 程序界面出现位置（纵坐标）
	 */
	public static final int LOC_Y = 50;

	Color color = Color.WHITE;
	Image offScreen = null; // 用于双缓冲

	// 持有其他类
	Board dataview = new Board(this); // 主界面类（显示监控数据主面板）

	/**
	 * 主方法
	 */
	public static void main(String[] args) {
		new Monitor().launchFrame();
	}

	/**
	 * 显示主界面
	 */
	public void launchFrame() {
		this.setBounds(LOC_X, LOC_Y, WIDTH, HEIGHT); // 设定程序在桌面出现的位置
		this.setTitle("实时数据采集监控"); // 设置程序标题
		this.setBackground(Color.white); // 设置背景色

		this.addWindowListener(new WindowAdapter() {
			// 添加对窗口状态的监听
			public void windowClosing(WindowEvent arg0) {
				// 当窗口关闭时
				System.exit(0); // 退出程序
			}
		});

		this.addKeyListener(new KeyMonitor()); // 添加键盘监听器
		this.setResizable(false); // 窗口大小不可更改
		this.setVisible(true); // 显示窗口

		new Thread(new RepaintThread()).start(); // 开启重画线程
	}

	/**
	 * 画出程序界面各组件元素
	 */
	public void paint(Graphics g) {
		g.setFont(new Font("微软雅黑", Font.BOLD, 40));
		g.setColor(Color.black);
		g.drawString("上位机实时数据采集监控系统", 540, 190);

		g.setFont(new Font("微软雅黑", Font.ITALIC, 26));
		g.drawString("Version：1.0", 730, 260);

		g.setFont(new Font("微软雅黑", Font.BOLD, 26));
		g.drawString("————点击Enter键进入主界面————", 550, 680);
	}

	/**
	 * 双缓冲方式重画界面各元素组件
	 */
	public void update(Graphics g) {
		if (offScreen == null)
			offScreen = this.createImage(WIDTH, HEIGHT);
		Graphics gOffScreen = offScreen.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.white);
		gOffScreen.fillRect(0, 0, WIDTH, HEIGHT); // 重画背景画布
		this.paint(gOffScreen); // 重画界面元素
		gOffScreen.setColor(c);
		g.drawImage(offScreen, 0, 0, null); // 将新画好的画布“贴”在原画布上
	}

	/*
	 * 内部类形式实现对键盘事件的监听
	 */
	private class KeyMonitor extends KeyAdapter {

		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();
			if (keyCode == KeyEvent.VK_ENTER) { // 当监听到用户敲击键盘enter键后执行下面的操作
				setVisible(false); // 隐去欢迎界面
				dataview.setVisible(true); // 显示监测界面
				dataview.dataFrame(); // 初始化监测界面
			}
		}
	}

	/*
	 * 重画线程（每隔250毫秒重画一次）
	 */
	private class RepaintThread implements Runnable {
		public void run() {
			while (true) {
				repaint();
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// 重画线程出错抛出异常时创建一个Dialog并显示异常详细信息
					JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				}
			}
		}
	}
}