/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java2ddrawingapplication;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JSpinner;


public class DrawingApplicationFrame extends JFrame
{

    // Create the panels for the top of the application. One panel for each
    // line and one to contain both of those panels.
    JPanel firstLine = new JPanel();
    JPanel secondLine = new JPanel();
    JPanel topPanel = new JPanel();  
    
    // create the widgets for the firstLine Panel.
    JLabel shapeJLabel = new JLabel("Shape: ");
    JComboBox<String> shapeComboBox = new JComboBox(new String[] {"Line", "Rectangle", "Oval"});
    JButton firstColorButton = new JButton("1st Color...");
    JButton secondColorButton = new JButton("2nd Color...");
    JButton undoButton = new JButton("Undo");
    JButton clearButton = new JButton("Clear");
    
    //create the widgets for the secondLine Panel.
    JLabel optionsJLabel = new JLabel("Options: ");
    JCheckBox filledCheckBox = new JCheckBox("Filled");
    JCheckBox gradientCheckBox = new JCheckBox("Use Gradient");
    JCheckBox dashedCheckBox = new JCheckBox("Dashed");
    JLabel widthJLabel = new JLabel("Line Width: ");
    JSpinner widthSpinner = new JSpinner();
    JLabel dashLengthJLabel = new JLabel("Dash Length: ");
    JSpinner dashLengthSpinner = new JSpinner();
    
    // Variables for drawPanel
    DrawPanel drawPanel = new DrawPanel();
    ArrayList<MyShapes> shapes = new ArrayList<>();
    Color firstColor = Color.BLACK;
    Color secondColor = Color.BLACK;
    int xCoord;
    int yCoord;
    
    // add status label
    JPanel statusPanel = new JPanel();
    JLabel locationJLabel = new JLabel("(" + xCoord + "," + yCoord + ")");
    
    // Constructor for DrawingApplicationFrame
    public DrawingApplicationFrame()
    {
        this.setLayout(new BorderLayout());
        topPanel.setLayout(new BorderLayout());
        
        // add widgets to panels
        // firstLine widgets
        firstLine.add(shapeJLabel);
        firstLine.add(shapeComboBox);
        firstLine.add(firstColorButton);
        firstLine.add(secondColorButton);
        firstLine.add(undoButton);
        firstLine.add(clearButton);
        firstLine.setBackground(Color.CYAN);
        
        // secondLine widgets
        secondLine.add(optionsJLabel);
        secondLine.add(filledCheckBox);
        secondLine.add(gradientCheckBox);
        secondLine.add(dashedCheckBox);
        secondLine.add(widthJLabel);
        secondLine.add(widthSpinner);
        secondLine.add(dashLengthJLabel);
        secondLine.add(dashLengthSpinner);
        secondLine.setBackground(Color.CYAN);
        
        // add top panel of two panels
        topPanel.add(firstLine, BorderLayout.NORTH);
        topPanel.add(secondLine, BorderLayout.SOUTH);
        
        // add topPanel to North, drawPanel to Center, and statusLabel to South
        
        add(topPanel, BorderLayout.NORTH);
        add(drawPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setBackground(Color.LIGHT_GRAY);
        statusPanel.add(locationJLabel, BorderLayout.WEST);
        //add listeners and event handlers
        firstColorButton.addActionListener(listener -> {
            firstColor = JColorChooser.showDialog(null, "Select First Color", firstColor);
            if (firstColor == null)
            {
                firstColor = Color.BLACK;
            }
        });
        secondColorButton.addActionListener(listener -> {
            firstColor = JColorChooser.showDialog(null, "Select Second Color", secondColor);
            if (secondColor == null)
            {
                secondColor = Color.BLACK;
            }
        });
        undoButton.addActionListener(listener -> {
            if(!shapes.isEmpty())
            {
                shapes.remove(shapes.size() - 1);
                drawPanel.repaint();
            }
        });
        clearButton.addActionListener(listener -> {
            shapes.clear();
            drawPanel.repaint();
        });
        
    }
    
    
    
    // Create event handlers, if needed
    
    // Create a private inner class for the DrawPanel.
    private class DrawPanel extends JPanel
    {
        
        Point start;
        ArrayList<MyShapes> currentShapes = new ArrayList<>();
        
        public DrawPanel()
        {
            setBackground(Color.WHITE);
            addMouseListener(new MouseHandler());
            addMouseMotionListener(new MouseHandler());
        }
        
        private MyShapes drawShape(Point start, Point end)
        {
            BasicStroke strk = dashedCheckBox.isSelected() ? new BasicStroke(Integer.parseInt(widthSpinner.getValue().toString()), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, new float[]{Float.parseFloat(dashLengthSpinner.getValue().toString())}, 0) : new BasicStroke(Integer.parseInt(widthSpinner.getValue().toString()), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND); 
            Paint paint = gradientCheckBox.isSelected() ? new GradientPaint(0, 0, firstColor, 50, 50, secondColor, true) : new GradientPaint(0, 0, firstColor, 50, 50, firstColor, true);

            switch(shapeComboBox.getSelectedItem().toString())
            {
                case "Line": return new MyLine(start, end, paint, strk);
                case "Oval": return new MyOval(start, end, paint, strk, filledCheckBox.isSelected());
                case "Rectangle": return new MyRectangle(start, end, paint, strk, filledCheckBox.isSelected());
                default: return null;
            }
        }
        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            //loop through and draw each shape in the shapes arraylist
            for (MyShapes shape : shapes){
                shape.draw(g2d);
            }
            for (MyShapes shape : currentShapes){
                shape.draw(g2d);
            }
            currentShapes.clear();
        }


        private class MouseHandler extends MouseAdapter implements MouseMotionListener
        {
            @Override
            public void mousePressed(MouseEvent event)
            {
                start = event.getPoint();
            }
            @Override
            public void mouseReleased(MouseEvent event)
            {
                MyShapes currentShape = drawShape(start, event.getPoint());
                if (currentShape != null){
                    shapes.add(currentShape);
                    drawPanel.repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent event)
            {
                locationJLabel.setText("(" + event.getX() + "," + event.getY() + ")");
                MyShapes currentShape = drawShape(start, event.getPoint());
                if (currentShape != null){
                    currentShapes.add(currentShape);
                    drawPanel.repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent event)
            {
                locationJLabel.setText("(" + event.getX() + "," + event.getY() + ")");
            }
        }

    }
}

