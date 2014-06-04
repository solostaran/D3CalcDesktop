package jopc.d3calc.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import jodroid.d3obj.D3HeroLite;

@SuppressWarnings("serial")
public class D3HeroRenderer extends JPanel implements
		ListCellRenderer<D3HeroLite> {

	protected static Border focusBorder = new BevelBorder(BevelBorder.RAISED);
	
	private JLabel lblName;
	private JLabel lblDesc;
	private JLabel lblParagon;

	public D3HeroRenderer() {
		setOpaque(true);
		SpringLayout sp = new SpringLayout();
		setLayout(sp);
		
		setPreferredSize(new Dimension(100,40));
		
		lblName = new JLabel("heroname");
		sp.putConstraint(SpringLayout.WEST, lblName, 2, SpringLayout.WEST, this);
		sp.putConstraint(SpringLayout.NORTH, lblName, 2, SpringLayout.NORTH, this);
		add(lblName);
		
		lblDesc = new JLabel("herodesc");
		lblDesc.setFont(new Font("Serif", Font.ITALIC, 11));
		sp.putConstraint(SpringLayout.EAST, lblDesc, -2, SpringLayout.EAST, this);
		sp.putConstraint(SpringLayout.SOUTH, lblDesc, -2, SpringLayout.SOUTH, this);
		add(lblDesc);
		
		lblParagon = new JLabel("paragon");
		lblParagon.setFont(new Font("Serif", Font.ITALIC, 11));
		lblParagon.setForeground(Color.BLUE);
		sp.putConstraint(SpringLayout.EAST, lblParagon, -2, SpringLayout.EAST, this);
		sp.putConstraint(SpringLayout.NORTH, lblParagon, 2, SpringLayout.NORTH, this);
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends D3HeroLite> list, D3HeroLite value, int index,
			boolean isSelected, boolean cellHasFocus) {
		
		lblName.setText(value.name);
		lblDesc.setText("level "+value.level+" "+value.getGender()+" "+value._class);
		lblParagon.setText(value.getParagon());
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setBorder(isSelected ? focusBorder : null);
		return this;
	}

}
