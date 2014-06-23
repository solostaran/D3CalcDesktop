package jopc.d3calc.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import jodroid.d3calc.R;
import jodroid.d3obj.D3IconImpl;
import jodroid.d3obj.D3Obj;
import jodroid.d3obj.D3Rune;
import jodroid.d3obj.D3SkillActive;
import jodroid.d3obj.D3SkillPassive;
import jodroid.d3obj.ID3Skill;

/**
 * TODO: D3ItemLite -> D3Skill ou nouvelle interface spécialisée
 * Déterminer la différence D3SkillActive et D3SkillPassive
 * Ne pas oublier que les Skills peuvent ne pas être présentes
 * @author JRD
 */

@SuppressWarnings("serial")
public class D3SkillRenderer extends JPanel implements
ListCellRenderer<ID3Skill> {

	private static final int LBL_DEFAULT_HEIGHT = 17;
	private static final int TXTLINE_DEFAULT_HEIGHT = 15;

	protected static Border focusBorder = new BevelBorder(BevelBorder.RAISED);

	protected static TitledBorder titledBorder = new TitledBorder(LineBorder.createGrayLineBorder(),
			"title");

	private JLabel lblIcon;
	private JLabel lblSkillName;
	private JTextArea txtSkillDesc;
	private JLabel lblSeparation;
	private JLabel lblRuneName;
	private JTextArea txtRuneDesc;
	
	public D3SkillRenderer() {
		setOpaque(true);
		BuildSpringLayout();
	}
	
	public void BuildSpringLayout() {
		SpringLayout sp = new SpringLayout();
		setLayout(sp);

		setPreferredSize(new Dimension(100,50));

		lblIcon = new JLabel();
		sp.putConstraint(SpringLayout.WEST, lblIcon, 2, SpringLayout.WEST, this);
		sp.putConstraint(SpringLayout.NORTH, lblIcon, -2, SpringLayout.NORTH, this);
		add(lblIcon);

		lblSkillName = new JLabel();
		lblSkillName.setFont(new Font("Serif", Font.BOLD, 12));
		sp.putConstraint(SpringLayout.WEST, lblSkillName, 2, SpringLayout.EAST, lblIcon);
		sp.putConstraint(SpringLayout.NORTH, lblSkillName, -2, SpringLayout.NORTH, this);
		add(lblSkillName);

		txtSkillDesc = new JTextArea();
		txtSkillDesc.setLineWrap(true);
		txtSkillDesc.setFont(new Font("Serif", Font.ITALIC, 11));
		sp.putConstraint(SpringLayout.WEST, txtSkillDesc, 2, SpringLayout.WEST, lblSkillName);
		sp.putConstraint(SpringLayout.EAST, txtSkillDesc, -2, SpringLayout.EAST, this);
		sp.putConstraint(SpringLayout.NORTH, txtSkillDesc, -2, SpringLayout.SOUTH, lblSkillName);
		add(txtSkillDesc);

		lblSeparation = new JLabel("+");
		sp.putConstraint(SpringLayout.WEST, lblSeparation, 0, SpringLayout.WEST, lblSkillName);
		sp.putConstraint(SpringLayout.NORTH, lblSeparation, -2, SpringLayout.SOUTH, txtSkillDesc);
		add(lblSeparation);

		lblRuneName = new JLabel();
		lblRuneName.setFont(new Font("Serif", Font.BOLD, 12));
		sp.putConstraint(SpringLayout.WEST, lblRuneName, 0, SpringLayout.WEST, lblSkillName);
		sp.putConstraint(SpringLayout.NORTH, lblRuneName, -2, SpringLayout.SOUTH, lblSeparation);
		add(lblRuneName);

		txtRuneDesc = new JTextArea();
		txtRuneDesc.setLineWrap(true);
		txtRuneDesc.setFont(new Font("Serif", Font.ITALIC, 11));
		sp.putConstraint(SpringLayout.WEST, txtRuneDesc, 0, SpringLayout.WEST, lblSkillName);
		sp.putConstraint(SpringLayout.EAST, txtRuneDesc, -2, SpringLayout.EAST, this);
		sp.putConstraint(SpringLayout.NORTH, txtRuneDesc, -2, SpringLayout.SOUTH, lblRuneName);
		add(txtRuneDesc);
	}
	
//	public void BuildBorderBoxLayout() {
//		setPreferredSize(new Dimension(100,50));
//		
//		BorderLayout bl = new BorderLayout();
//		setLayout(bl);
//		
//		lblIcon = new JLabel();
//		add(lblIcon, BorderLayout.WEST);
//		
//		JPanel panel = new JPanel();
//		add(panel, BorderLayout.CENTER);
//		
//		BoxLayout boxl = new BoxLayout(panel, BoxLayout.Y_AXIS);
//		panel.setLayout(boxl);
//		
//		lblSkillName = new JLabel();
//		panel.add(lblSkillName);
//		txtSkillDesc = new JTextArea();
//		txtSkillDesc.setLineWrap(true);
//		panel.add(txtSkillDesc);
//		lblSeparation = new JLabel();
//		panel.add(lblSeparation);
//		lblRuneName = new JLabel();
//		panel.add(lblRuneName);
//		txtRuneDesc = new JTextArea();
//		txtRuneDesc.setLineWrap(true);
//		panel.add(txtRuneDesc);
//	}

//	private static int countLines(JTextArea textArea) {
//		if (textArea.getText().isEmpty()) return 0;
//		AttributedString text = new AttributedString(textArea.getText());
//		FontRenderContext frc = textArea.getFontMetrics(textArea.getFont())
//				.getFontRenderContext();
//		AttributedCharacterIterator charIt = text.getIterator();
//		LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(charIt, frc);
//		float formatWidth = (float) textArea.getSize().width;
//		lineMeasurer.setPosition(charIt.getBeginIndex());
//
//		int noLines = 0;
//		while (lineMeasurer.getPosition() < charIt.getEndIndex()) {
//			lineMeasurer.nextLayout(formatWidth);
//			noLines++;
//		}
//
//		return noLines;
//	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends ID3Skill> list, ID3Skill value, int index,
			boolean isSelected, boolean cellHasFocus) {

		D3Rune rune = null;
		if (value instanceof D3SkillActive) {
			titledBorder.setTitle(D3Obj.getContext().getString(R.string.title_active_skill)+" n°"+(index+1));
			rune = ((D3SkillActive)value).rune;
		}
		if (value instanceof D3SkillPassive) {
			titledBorder.setTitle(D3Obj.getContext().getString(R.string.title_passive_skill)+" n°"+(index-5));
		}

		lblIcon.setIcon(null);
		txtSkillDesc.setText("");
		lblSeparation.setText("");
		lblRuneName.setText("");
		txtRuneDesc.setText("");
		if (value.getSkill() == null) {
			lblSkillName.setText("skill undefined");
			return this;
		}

		setBorder(titledBorder);
		lblSkillName.setText(value.getSkill().name);
		txtSkillDesc.setText(value.getSkill().description);

		int height = 50+LBL_DEFAULT_HEIGHT+txtSkillDesc.getLineCount()*TXTLINE_DEFAULT_HEIGHT;
		if (rune != null) {
			lblSeparation.setText("+");
			lblRuneName.setText(rune.name);
			txtRuneDesc.setText(rune.description);
			height += LBL_DEFAULT_HEIGHT*2+txtRuneDesc.getLineCount()*TXTLINE_DEFAULT_HEIGHT;
		}
		setPreferredSize(new Dimension(100, height));

		if (value.getSkill().iconSmall != null) {
			D3IconImpl icon = (D3IconImpl)value.getSkill().iconSmall;
			lblIcon.setIcon(new ImageIcon(icon.icon.image));
		}

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
			txtSkillDesc.setBackground(list.getSelectionBackground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
			txtSkillDesc.setBackground(list.getBackground());
		}
		return this;
	}

}
