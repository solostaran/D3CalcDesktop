package jopc.d3calc.gui;

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

import jodroid.d3obj.D3ProfileLite;

@SuppressWarnings("serial")
public class D3ProfileRenderer extends JPanel implements
		ListCellRenderer<D3ProfileLite> {

	protected static Border focusBorder = new BevelBorder(BevelBorder.RAISED);
	
	private JLabel lblTag;
	private JLabel lblHost;

	public D3ProfileRenderer() {
		setOpaque(true);
		SpringLayout sp = new SpringLayout();
		setLayout(sp);
		
		setPreferredSize(new Dimension(100,40));
		
		lblTag = new JLabel("battletag");
		sp.putConstraint(SpringLayout.WEST, lblTag, 2, SpringLayout.WEST, this);
		sp.putConstraint(SpringLayout.NORTH, lblTag, 2, SpringLayout.NORTH, this);
		add(lblTag);
		
		lblHost = new JLabel("battlehost");
		lblHost.setFont(new Font("Serif", Font.ITALIC, 11));
		sp.putConstraint(SpringLayout.EAST, lblHost, -2, SpringLayout.EAST, this);
		sp.putConstraint(SpringLayout.SOUTH, lblHost, -2, SpringLayout.SOUTH, this);
		add(lblHost);
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends D3ProfileLite> list, D3ProfileLite value, int index,
			boolean isSelected, boolean cellHasFocus) {
		lblTag.setText(value.battleTag.replace("-", "#"));
		lblHost.setText(value.battlehost);
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
