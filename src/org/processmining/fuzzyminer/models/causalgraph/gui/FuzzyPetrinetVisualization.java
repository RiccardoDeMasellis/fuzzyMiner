package org.processmining.fuzzyminer.models.causalgraph.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.BasicMarqueeHandler;
import org.processmining.framework.util.Cleanable;
import org.processmining.framework.util.ui.scalableview.VerticalLabelUI;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.models.jgraph.ContextMenuCreator;
import org.processmining.models.jgraph.ProMGraphModel;
import org.processmining.models.jgraph.ProMJGraph;
import org.processmining.models.jgraph.elements.ProMGraphCell;
import org.processmining.models.jgraph.elements.ProMGraphEdge;
import org.processmining.models.jgraph.listeners.SelectionListener;

import com.fluxicon.slickerbox.factory.SlickerDecorator;
import com.fluxicon.slickerbox.factory.SlickerFactory;

public class FuzzyPetrinetVisualization extends JPanel implements FuzzyGraphVisualization, Cleanable,
ChangeListener, ActionListener {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected static final int MAX_ZOOM = 1200;
	
	// -------------------------------------------

	protected ProMJGraph graph;
	protected ProMJGraph pipGraph;

	
	
	
	protected JScrollPane scroll;
	private JPanel pipPanelON, pipPanelOFF;
	private PIPPanel pip;
	private JPanel zoomPanelON, zoomPanelOFF;
	private ZoomPanel zoom;
	//private JPanel parametersPanelON, parametersPanelOFF;
	//private ParametersPanel parameters;
	//private JPanel setupPanelON, setupPanelOFF;
	//private SetupPanel setup;
	private JPanel fitnessPanel;

	private float zoomRatio, pipRatio;
	private double normalScale;
	private Rectangle normalBounds, zoomBounds, pipBounds;

	private boolean hasNodeSelected;

	private List<SelectionListener<?, ?>> selectionListeners = new ArrayList<SelectionListener<?, ?>>(
			0);
	private ContextMenuCreator creator = null;
	
	
	
	
	
	
	public FuzzyPetrinetVisualization(final ProMJGraph graph) {
		// TODO Auto-generated constructor stub
		
		this.setLayout(null);
		this.graph = graph;

		SlickerFactory factory = SlickerFactory.instance();
		SlickerDecorator decorator = SlickerDecorator.instance();

		this.addComponentListener(new java.awt.event.ComponentListener() {

			public void componentHidden(ComponentEvent e) {
			}

			public void componentMoved(ComponentEvent e) {
			}

			public void componentShown(ComponentEvent e) {
			}

			public void componentResized(ComponentEvent e) {
				resize();
			}
		});

		this.initGraph();

		AdjustmentListener aListener = new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				repaintPIP(graph.getVisibleRect());
			}
		};
		MouseListener mListener = new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {


			}

			public void mouseExited(MouseEvent e) {

				if (hasNodeSelected) {

					scroll.repaint();

				}

			}

			public void mousePressed(MouseEvent e) {

				zoomPanelOFF.setVisible(false);
				zoomPanelOFF.setEnabled(false);

				pipPanelOFF.setVisible(false);
				pipPanelOFF.setEnabled(false);

				//setupPanelOFF.setVisible(false);
				//setupPanelOFF.setEnabled(false);

				//parametersPanelOFF.setVisible(false);
				//parametersPanelOFF.setEnabled(false);
			}

			public void mouseReleased(MouseEvent e) {

				zoomPanelOFF.setVisible(true);
				zoomPanelOFF.setEnabled(true);

				pipPanelOFF.setVisible(true);
				pipPanelOFF.setEnabled(true);

				//setupPanelOFF.setVisible(true);
				//setupPanelOFF.setEnabled(true);

				//parametersPanelOFF.setVisible(true);
				//parametersPanelOFF.setEnabled(true);
			}
		};

		this.scroll = new JScrollPane(graph);
		this.scroll.getHorizontalScrollBar().addAdjustmentListener(aListener);
		this.scroll.getVerticalScrollBar().addAdjustmentListener(aListener);
		this.scroll.getHorizontalScrollBar().addMouseListener(mListener);
		this.scroll.getVerticalScrollBar().addMouseListener(mListener);
		decorator.decorate(this.scroll, Color.WHITE, Color.GRAY,
				Color.DARK_GRAY);
		this.scroll.setBorder(new DashedBorder(Color.LIGHT_GRAY));

		this.pipPanelON = factory.createRoundedPanel(15, Color.LIGHT_GRAY);
		this.pipPanelOFF = factory.createRoundedPanel(15, Color.DARK_GRAY);
		this.pipPanelON.setLayout(null);
		this.pipPanelOFF.setLayout(null);
		this.pip = new PIPPanel(factory, this.scroll, this.pipGraph, this);
		this.pip.setRect(graph.getBounds());
		this.pipPanelON.add(this.pip);
		this.pipPanelON.setVisible(false);
		this.pipPanelON.setEnabled(false);
		JLabel pipPanelTitle = factory.createLabel("PIP");
		pipPanelTitle.setForeground(Color.WHITE);
		pipPanelTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD,
				18));
		// pipPanelTitle.setUI(new VerticalLabelUI(true));
		this.pipPanelOFF.add(pipPanelTitle);
		pipPanelTitle.setBounds(10, 10, 30, 30);

		this.pipPanelOFF.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {

				int x = scroll.getHorizontalScrollBar().getValue();
				int y = scroll.getVerticalScrollBar().getValue();

				//showSetup(false);
				//showParameters(false);
				// showOptions(false);
				showZoom(false);
				showPIP(true);

				scroll.getHorizontalScrollBar().setValue((int) (x * pipRatio));
				scroll.getVerticalScrollBar().setValue((int) (y * pipRatio));

				if (hasNodeSelected) {

				}
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		});

		this.pipPanelON.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {

				int x = scroll.getHorizontalScrollBar().getValue();
				int y = scroll.getVerticalScrollBar().getValue();

				boolean flag = false;
				if ((e.getY() >= pipPanelON.getHeight()) || (e.getY() <= 10))
					flag = true;
				else {

					if ((e.getX() >= pipPanelON.getWidth()) || (e.getX() <= 0))
						flag = true;
				}

				if (flag) {

					showPIP(false);

					scroll.getHorizontalScrollBar().setValue(
							(int) (x / pipRatio));
					scroll.getVerticalScrollBar()
							.setValue((int) (y / pipRatio));

					if (hasNodeSelected) {


					}
				}
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		});

		this.zoomPanelON = factory.createRoundedPanel(15, Color.LIGHT_GRAY);
		this.zoomPanelOFF = factory.createRoundedPanel(15, Color.DARK_GRAY);
		this.zoomPanelON.setLayout(null);
		this.zoomPanelOFF.setLayout(null);
		this.zoom = new ZoomPanel(factory, decorator, 100, MAX_ZOOM);
		this.zoom.addSliderChangeListener(this);
		this.zoomPanelON.add(this.zoom);
		this.zoomPanelON.setVisible(false);
		this.zoomPanelON.setEnabled(false);
		JLabel zoomPanelTitle = factory.createLabel("Zoom");
		zoomPanelTitle.setForeground(Color.WHITE);
		zoomPanelTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD,
				18));
		zoomPanelTitle.setUI(new VerticalLabelUI(true));
		this.zoomPanelOFF.add(zoomPanelTitle);
		zoomPanelTitle.setBounds(10, 10, 30, 55);

		this.zoomPanelOFF.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {

				int x = scroll.getHorizontalScrollBar().getValue();
				int y = scroll.getVerticalScrollBar().getValue();

				//showSetup(false);
				//showParameters(false);
				// showOptions(false);
				showPIP(false);
				showZoom(true);

				scroll.getHorizontalScrollBar().setValue((int) (x * zoomRatio));
				scroll.getVerticalScrollBar().setValue((int) (y * zoomRatio));

				if (hasNodeSelected) {

				}
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		});

		this.zoomPanelON.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {

				int x = scroll.getHorizontalScrollBar().getValue();
				int y = scroll.getVerticalScrollBar().getValue();

				boolean flag = false;
				if (e.getX() >= zoom.getWidth())
					flag = true;
				else {

					if ((e.getY() >= zoom.getHeight()) || (e.getY() <= 0))
						flag = true;
				}

				if (flag) {

					showZoom(false);

					scroll.getHorizontalScrollBar().setValue(
							(int) (x / zoomRatio));
					scroll.getVerticalScrollBar().setValue(
							(int) (y / zoomRatio));

					if (hasNodeSelected) {


					}
				}
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		});

//		if (this.net instanceof SimpleHeuristicsNet) {

			/*this.parametersPanelON = factory.createRoundedPanel(15,
					Color.LIGHT_GRAY);
			this.parametersPanelOFF = factory.createRoundedPanel(15,
					Color.DARK_GRAY);
			this.parametersPanelON.setLayout(null);
			this.parametersPanelOFF.setLayout(null);
			this.parameters = new ParametersPanel();
;

			this.parameters.setBackground(Color.LIGHT_GRAY);
			this.parameters.setEnabled(false);
			this.parametersPanelON.add(this.parameters);
			this.parametersPanelON.setVisible(false);
			this.parametersPanelON.setEnabled(false);
			JLabel parametersPanelTitle = factory.createLabel("Parameters");
			parametersPanelTitle.setForeground(Color.WHITE);
			parametersPanelTitle.setFont(new java.awt.Font("Dialog",
					java.awt.Font.BOLD, 18));
			this.parametersPanelOFF.add(parametersPanelTitle);
			parametersPanelTitle.setBounds(12, 0, 105, 30);

			this.parametersPanelOFF.addMouseListener(new MouseListener() {

				public void mouseClicked(MouseEvent e) {
				}

				public void mouseEntered(MouseEvent e) {

					//showSetup(false);
					// showOptions(false);
					showPIP(false);
					showZoom(false);
					//showParameters(true);

					if (hasNodeSelected) {


					}
				}

				public void mouseExited(MouseEvent e) {
				}

				public void mousePressed(MouseEvent e) {
				}

				public void mouseReleased(MouseEvent e) {
				}
			});

			this.parametersPanelON.addMouseListener(new MouseListener() {

				public void mouseClicked(MouseEvent e) {
				}

				public void mouseEntered(MouseEvent e) {
				}

				public void mouseExited(MouseEvent e) {

					boolean flag = false;
					if ((e.getX() >= parametersPanelON.getWidth())
							|| (e.getX() <= 0))
						flag = true;
					else {

						if ((e.getY() >= parametersPanelON.getHeight())
								|| (e.getY() <= 0))
							flag = true;
					}

					if (flag) {

						//showParameters(false);

						if (hasNodeSelected) {


						}
					}
				}

				public void mousePressed(MouseEvent e) {
				}

				public void mouseReleased(MouseEvent e) {
				}
			});*/
//		} else
//			this.parametersPanelON = new JPanel();

		// this.optionsPanelOFF = factory.createRoundedPanel(15,
		// Color.DARK_GRAY);
		// this.optionsPanelOFF.setLayout(null);
		// JLabel optionsPanelTitle = factory.createLabel("Options");
		// optionsPanelTitle.setForeground(Color.WHITE);
		// optionsPanelTitle.setFont(new java.awt.Font("Dialog",
		// java.awt.Font.BOLD, 18));
		// this.optionsPanelOFF.add(optionsPanelTitle);
		// optionsPanelTitle.setBounds(10, 10, 70, 30);

		/*this.setupPanelON = factory.createRoundedPanel(15, Color.LIGHT_GRAY);
		this.setupPanelOFF = factory.createRoundedPanel(15, Color.DARK_GRAY);
		this.setupPanelON.setLayout(null);
		this.setupPanelOFF.setLayout(null);
		this.setup = new SetupPanel(factory, decorator, settings);
		this.setupPanelON.add(this.setup);
		this.setupPanelON.setVisible(false);
		this.setupPanelON.setEnabled(false);
		JLabel setupPanelTitle = factory.createLabel("Setup");
		setupPanelTitle.setForeground(Color.WHITE);
		setupPanelTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD,
				18));
		setupPanelTitle.setUI(new VerticalLabelUI(false));
		this.setupPanelOFF.add(setupPanelTitle);
		setupPanelTitle.setBounds(0, 7, 30, 55);

		this.setupPanelOFF.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {

				// showOptions(false);
				showPIP(false);
				showZoom(false);
				showParameters(false);
				showSetup(true);

				if (hasNodeSelected) {

					joinsPanel.setVisible(false);
					joinsPanel.setEnabled(false);

					splitsPanel.setVisible(false);
					splitsPanel.setEnabled(false);
				}
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		});

		this.setupPanelON.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {

				boolean flag = true;
				if ((e.getX() >= setupPanelON.getWidth()) || (e.getX() <= 0))
					flag = false;
				else {

					if ((e.getY() >= setupPanelON.getHeight())
							|| (e.getY() <= 0))
						flag = false;
				}

				if (!flag) {

					showSetup(flag);

					if (setup.hasChanged()) {

						redraw();
						hasNodeSelected = false;
					} else {

						if (hasNodeSelected) {

							joinsPanel.repaint();
							splitsPanel.repaint();

							joinsPanel.setVisible(true);
							joinsPanel.setEnabled(true);

							splitsPanel.setVisible(true);
							splitsPanel.setEnabled(true);
						}
					}
				}
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		});*/

		this.fitnessPanel = factory.createRoundedPanel(15, Color.GRAY);
		this.fitnessPanel.setLayout(null);
/*		JLabel fitnessInfo = factory.createLabel("Fitness: "
				+ (Math.round(this.net.getFitness() * 10000) / 10000f));
		fitnessInfo.setForeground(Color.WHITE);
		fitnessInfo
				.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 18));
		this.fitnessPanel.add(fitnessInfo);
		fitnessInfo.setBounds(20, 0, 140, 30);*/

		/*this.joinsPanel = factory.createRoundedPanel(15, Color.LIGHT_GRAY);
		this.joinsPanel.setLayout(null);
		this.joins = new AnnotationsPanel(factory, decorator, null, "");
		this.joinsPanel.add(this.joins);
		this.joinsPanel.setVisible(false);
		this.joinsPanel.setEnabled(false);

		this.splitsPanel = factory.createRoundedPanel(15, Color.LIGHT_GRAY);
		this.splitsPanel.setLayout(null);
		this.splits = new AnnotationsPanel(factory, decorator, null, "");
		this.splitsPanel.add(this.splits);
		this.splitsPanel.setVisible(false);
		this.splitsPanel.setEnabled(false);*/

		this.add(this.zoomPanelON);
		this.add(this.zoomPanelOFF);
		this.add(this.pipPanelON);
		this.add(this.pipPanelOFF);
		//this.add(this.parametersPanelON);
		//this.add(this.parametersPanelOFF);
		// this.add(this.optionsPanelON);
		// this.add(this.optionsPanelOFF);
		/*this.add(this.setupPanelON);
		this.add(this.setupPanelOFF);
		this.add(this.fitnessPanel);
		this.add(this.joinsPanel);
		this.add(this.splitsPanel);*/
		this.add(this.scroll);

		this.setBackground(Color.WHITE);

		this.validate();
		this.repaint();
	}

	private void resize() {

		int width = this.getSize().width;
		int height = this.getSize().height;

		int pipHeight = 250;
		int pipWidth = (int) ((float) width / (float) height * pipHeight);
		this.pip.setBounds(10, 20, pipWidth, pipHeight);

		this.zoom.setHeight((int) (height * 0.66));

		int zoomWidth = this.zoom.getSize().width;
		int zoomHeight = this.zoom.getSize().height;

		this.pipRatio = (float) (height - pipHeight - 50)
				/ (float) (height - 60);
		this.zoomRatio = (float) (width - zoomWidth - 40)
				/ (float) (width - 60);
		this.normalBounds = new Rectangle(30, 30, width - 60, height - 60);
		this.zoomBounds = new Rectangle(10 + zoomWidth,
				30 + (int) ((1f - this.zoomRatio) * (height - 60)), width
						- zoomWidth - 40,
				(int) (this.zoomRatio * (height - 60)));
		this.pipBounds = new Rectangle(
				30 + ((int) ((1f - this.pipRatio) * (width - 60))),
				20 + pipHeight, (int) (this.pipRatio * (width - 60)), height
						- pipHeight - 50);

		this.normalScale = graph.getScale();

		this.scroll.setBounds(this.normalBounds);
		this.pipPanelON.setBounds(40, -10, pipWidth + 20, pipHeight + 30);
		this.pipPanelOFF.setBounds(40, -10, 50, 40);
		this.zoomPanelON.setBounds(0, 40, zoomWidth + 10, zoomHeight);
		this.zoomPanelOFF.setBounds(-10, 40, 40, 72);

		/*int parametersHeight = this.parameters.getHeight();
		this.parametersPanelON.setBounds(width - 580,
				height - parametersHeight, 540, parametersHeight + 10);
		this.parametersPanelOFF.setBounds(width - 165, height - 30, 125, 40);*/

		// this.optionsPanelOFF.setBounds(width - 130, -10, 90, 40);

		//this.setup.setBounds(10, 10, 330, 420);
		/*this.setupPanelON.setBounds(width - 335, height - 370, 345, 330);
		this.setupPanelOFF.setBounds(width - 30, height - 115, 40, 75);

		this.fitnessPanel.setBounds(-10, height - 30, 160, 40);*/

		double fitRatio = scaleToFit(this.graph, this.scroll, false);
		this.zoom
				.setFitValue((int) Math.floor(fitRatio * this.zoomRatio * 100));
		this.scalePIP();

		/*this.joinsPanel.setBounds((int) (width / 2f) - 305, height - 300, 300,
				310);
		this.joins.setSize(300 - 20, 300 - 20);
		this.joins.setBounds(10, 10, 300 - 20, 300 - 20);

		this.splitsPanel.setBounds((int) (width / 2f) + 5, height - 300, 300,
				310);
		this.splits.setSize(300 - 20, 300 - 20);
		this.splits.setBounds(10, 10, 300 - 20, 300 - 20);*/
	}

	private void showZoom(boolean status) {

		zoomPanelOFF.setVisible(!status);
		zoomPanelOFF.setEnabled(!status);
		zoomPanelON.setVisible(status);
		zoomPanelON.setEnabled(status);

		if (status) {

			this.scroll.setBounds(this.zoomBounds);
			graph.setScale(this.normalScale * this.zoomRatio);
		} else {

			this.scroll.setBounds(this.normalBounds);
			graph.setScale(this.normalScale);
		}

	}

	private void showPIP(boolean status) {

		pipPanelOFF.setVisible(!status);
		pipPanelOFF.setEnabled(!status);
		pipPanelON.setVisible(status);
		pipPanelON.setEnabled(status);

		if (status) {

			this.scroll.setBounds(this.pipBounds);
			graph.setScale(this.normalScale * this.pipRatio);
		} else {

			this.scroll.setBounds(this.normalBounds);
			graph.setScale(this.normalScale);
		}
	}

/*	private void showParameters(boolean status) {

		parametersPanelOFF.setVisible(!status);
		parametersPanelOFF.setEnabled(!status);
		parametersPanelON.setVisible(status);
		parametersPanelON.setEnabled(status);
	}*/

	// private void showOptions(boolean status){
	//
	// optionsPanelOFF.setVisible(!status);
	// optionsPanelOFF.setEnabled(!status);
	// optionsPanelON.setVisible(status);
	// optionsPanelON.setEnabled(status);
	// }
	/*private void showSetup(boolean status) {

		setupPanelOFF.setVisible(!status);
		setupPanelOFF.setEnabled(!status);
		setupPanelON.setVisible(status);
		setupPanelON.setEnabled(status);
	}*/

	private void redraw() {

		int scrollPositionX = this.scroll.getHorizontalScrollBar().getValue();
		int scrollPositionY = this.scroll.getVerticalScrollBar().getValue();

		//AnnotatedVisualizationGenerator generator = new AnnotatedVisualizationGenerator();
		/*HeuristicsNetGraph hng = generator.generate(this.net, this.setup
				.getSettings());

		this.graph = FuzzyCausalGraphVisualizer.createJGraph(hng, new ViewSpecificAttributeMap(), null);*/

		this.initGraph();

		this.remove(this.scroll);

		AdjustmentListener aListener = new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {

				repaintPIP(graph.getVisibleRect());
			}
		};
		MouseListener mListener = new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {

				if (hasNodeSelected) {

				}
			}

			public void mouseExited(MouseEvent e) {

				if (hasNodeSelected) {

				}
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		};

		this.scroll = new JScrollPane(graph);
		SlickerDecorator.instance().decorate(this.scroll, Color.WHITE,
				Color.GRAY, Color.DARK_GRAY);
		this.scroll.getHorizontalScrollBar().addAdjustmentListener(aListener);
		this.scroll.getVerticalScrollBar().addAdjustmentListener(aListener);
		this.scroll.getHorizontalScrollBar().addMouseListener(mListener);
		this.scroll.getVerticalScrollBar().addMouseListener(mListener);
		this.scroll.setBorder(new DashedBorder(Color.LIGHT_GRAY));
		this.add(this.scroll);
		this.scroll.setBounds(this.normalBounds);

		this.pip.setPIPgraph(this.pipGraph);
		this.pip.setParentScroll(this.scroll);
		this.scalePIP();

		this.graph.setScale(this.normalScale);

		this.scroll.getHorizontalScrollBar().setValue(scrollPositionX);
		this.scroll.getVerticalScrollBar().setValue(scrollPositionY);
	}

	private void initGraph() {

		this.graph.addGraphSelectionListener(new GraphSelectionListener() {

			@SuppressWarnings("unchecked")
			public void valueChanged(GraphSelectionEvent e) {

				DirectedGraphNode selectedCell = null;

				Object[] cells = e.getCells();
				Collection nodesAdded = new ArrayList<ProMGraphCell>();
				Collection edgesAdded = new ArrayList<ProMGraphEdge>();
				Collection nodesRemoved = new ArrayList<ProMGraphCell>();
				Collection edgesRemoved = new ArrayList<ProMGraphEdge>();
				Collection<?> nodes = graph.getProMGraph().getNodes();
				Collection<?> edges = graph.getProMGraph().getEdges();
				for (int i = 0; i < cells.length; i++) {
					Collection nodeList;
					Collection edgeList;

					boolean isCell = cells[i] instanceof ProMGraphCell;
					boolean isEdge = cells[i] instanceof ProMGraphEdge;

					if (e.isAddedCell(i)) {
						nodeList = nodesAdded;
						edgeList = edgesAdded;

						if (isCell && (selectedCell == null))
							selectedCell = ((ProMGraphCell) cells[i]).getNode();

					} else {
						nodeList = nodesRemoved;
						edgeList = edgesRemoved;
					}
					if (isCell) {
						DirectedGraphNode node = ((ProMGraphCell) cells[i])
								.getNode();
						if (nodes.contains(node)) {
							nodeList.add(node);
						}
					} else if (isEdge) {
						DirectedGraphEdge<?, ?> edge = ((ProMGraphEdge) cells[i])
								.getEdge();
						if (edges.contains(edge)) {
							edgeList.add(((ProMGraphEdge) cells[i]).getEdge());
						}
					}
				}
				SelectionListener.SelectionChangeEvent event = new SelectionListener.SelectionChangeEvent(
						nodesAdded, edgesAdded, nodesRemoved, edgesRemoved);
				for (SelectionListener listener : selectionListeners) {
					listener.SelectionChanged(event);
				}

				// retrieve inputs/outputs for the selected event
/*				if (net instanceof AnnotatedHeuristicsNet) {

					AnnotatedHeuristicsNet anet = (AnnotatedHeuristicsNet) net;

					if (nodesAdded.size() == 1) {

						String nodeLabel = selectedCell.getLabel();
						int index1 = nodeLabel.indexOf("<b>") + 3;
						int index2 = nodeLabel.indexOf("<br />");

						String nodeID = nodeLabel.substring(index1, index2 - 4);

						nodeLabel = nodeLabel.substring(index2 + 6);
						String nodeType = nodeLabel.substring(0, nodeLabel.indexOf("<br />"));
						
						if (!"".equals(nodeType)) {
							nodeID = nodeID + "+" + nodeType;
						}

						String key = anet.getKey(nodeID).toString();
						splits.update(anet.getSplit(key), nodeID, anet
								.getInvertedKeys());
						joins.update(anet.getJoin(key), nodeID, anet
								.getInvertedKeys());

						joinsPanel.repaint();
						joinsPanel.setVisible(true);
						joinsPanel.setEnabled(true);
						splitsPanel.repaint();
						splitsPanel.setVisible(true);
						splitsPanel.setEnabled(true);

						hasNodeSelected = true;
					} else {

						joinsPanel.setVisible(false);
						joinsPanel.setEnabled(false);
						splitsPanel.setVisible(false);
						splitsPanel.setEnabled(false);

						hasNodeSelected = false;
					}
				}*/
			}

		});
		this.graph.setTolerance(4);

		this.graph.setMarqueeHandler(new BasicMarqueeHandler() {
			private boolean test(MouseEvent e) {
				return SwingUtilities.isRightMouseButton(e)
						&& (e.getModifiers() & InputEvent.ALT_MASK) == 0;

			}

			public boolean isForceMarqueeEvent(MouseEvent event) {
				if (test(event)) {
					return true;
				} else {
					return false;
				}
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				if (test(e)) {
					e.consume();
				} else {
					super.mouseReleased(e);
				}
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				if (test(e)) {
					synchronized (graph.getProMGraph()) {
						// Check for selection.
						// If the cell that is being clicked is part of the
						// selection,
						// we use the current selection.
						// otherwise, we use a new selection
						Object cell = graph.getFirstCellForLocation(e.getX(), e
								.getY());

						Collection<DirectedGraphElement> sel;
						if (cell == null) {
							// Nothing selected
							graph.clearSelection();
							sel = new ArrayList<DirectedGraphElement>(0);
						} else if (graph.getSelectionModel().isCellSelected(
								cell)) {
							// the current selection contains cell
							// use that selection
							sel = getSelectedElements();
						} else {
							// the current selection does not contain cell.
							// reset the selection to [cell]
							sel = new ArrayList<DirectedGraphElement>(1);
							sel.add(getElementForLocation(e.getX(), e.getY()));
							graph.setSelectionCell(cell);
						}
						if (creator != null) {
							JPopupMenu menu = creator.createMenuFor(graph
									.getProMGraph(), sel);
							if (menu != null) {
								menu.show(graph, e.getX(), e.getY());
							}
						}
					}
				} else {
					super.mousePressed(e);
				}
			}
		});

		this.graph.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {

				if (hasNodeSelected) {


				}
			}
		});

		// Collapse any expandable nodes that claim they are collapsed
		// This is not handled previously.
//		for (Object n : graph.getGraphLayoutCache().getCells(true, false,
//				false, false)) {
//			if (((ProMGraphCell) n).getNode() instanceof Expandable) {
//				Expandable ex = (Expandable) ((ProMGraphCell) n).getNode();
//				
//				if (ex.isCollapsed()) { ex.collapse(); }
//			}
//		}
		
		GraphLayoutConnection con = new GraphLayoutConnection(this.graph.getProMGraph());

		ProMGraphModel model = new ProMGraphModel(graph.getProMGraph());
		this.pipGraph = new ProMJGraph(model, true, graph.getViewSpecificAttributes(), con) {

			private static final long serialVersionUID = -4671278744184554287L;

			@Override
			protected void changeHandled() {

				scalePIP(); 
				repaintPIP(graph.getVisibleRect());
			}
		};

		this.hasNodeSelected = false;
	}

	public double getScale() {
		return graph.getScale();
	}

	public void setScale(double d) {

		int b = (int) (100.0 * d);
		b = Math.max(b, 1);
		b = Math.min(b, MAX_ZOOM);
		this.zoom.setValue(b);
	}

	protected void repaintPIP(Rectangle2D rect) {

		double s = factorMultiplyGraphToPIP();
		double x = Math.max(1, s * rect.getX());
		double y = Math.max(1, s * rect.getY());
		double w = Math.min(s * rect.getWidth(), this.pip.getVisWidth() - 1);
		double h = Math.min(s * rect.getHeight(), this.pip.getVisHeight() - 1);
		rect = new Rectangle2D.Double(x, y, w, h);
		this.pip.setRect(rect);
		this.pip.repaint();
	}

	public double factorMultiplyGraphToPIP() {
		return pipGraph.getScale() / graph.getScale();
	}

	protected void scalePIP() {
		this.pipGraph.setScale(scaleToFit(this.pipGraph, this.pip, false));
	}

	protected double scaleToFit(ProMJGraph graph, Container container,
			boolean reposition) {

		Rectangle2D bounds = graph.getBounds();
		double x = bounds.getX();
		double y = bounds.getY();
		if (reposition) {

			graph.repositionToOrigin();
			x = 0;
			y = 0;
		}

		Dimension size = container.getSize();

		double ratio = Math.min(size.getWidth() / (bounds.getWidth() + x), size
				.getHeight()
				/ (bounds.getHeight() + y));

		return ratio;
	}

	public void paint(Graphics g) {
		super.paint(g);
	}

	public DirectedGraphElement getElementForLocation(double x, double y) {
		Object cell = graph.getFirstCellForLocation(x, y);
		if (cell instanceof ProMGraphCell) {
			return ((ProMGraphCell) cell).getNode();
		}
		if (cell instanceof ProMGraphEdge) {
			return ((ProMGraphEdge) cell).getEdge();
		}
		return null;
	}

	public Collection<DirectedGraphNode> getSelectedNodes() {
		List<DirectedGraphNode> nodes = new ArrayList<DirectedGraphNode>();
		for (Object o : graph.getSelectionCells()) {
			if (o instanceof ProMGraphCell) {
				nodes.add(((ProMGraphCell) o).getNode());
			}
		}
		return nodes;
	}

	public Collection<DirectedGraphEdge<?, ?>> getSelectedEdges() {
		List<DirectedGraphEdge<?, ?>> edges = new ArrayList<DirectedGraphEdge<?, ?>>();
		for (Object o : graph.getSelectionCells()) {
			if (o instanceof ProMGraphEdge) {
				edges.add(((ProMGraphEdge) o).getEdge());
			}
		}
		return edges;
	}

	public Collection<DirectedGraphElement> getSelectedElements() {
		List<DirectedGraphElement> elements = new ArrayList<DirectedGraphElement>();
		for (Object o : graph.getSelectionCells()) {
			if (o instanceof ProMGraphCell) {
				elements.add(((ProMGraphCell) o).getNode());
			} else if (o instanceof ProMGraphEdge) {
				elements.add(((ProMGraphEdge) o).getEdge());
			}
		}
		return elements;
	}

	public void cleanUp() {

		graph.cleanUp();
		pipGraph.cleanUp();
	}

	public void stateChanged(ChangeEvent e) {

		Object source = e.getSource();

		if (source instanceof JSlider) {

			graph.setScale(((JSlider) source).getValue() / 100.0);
			repaintPIP(graph.getVisibleRect());

			this.normalScale = graph.getScale() / this.zoomRatio;
		}
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finalize() throws Throwable {

		try {
			cleanUp();
		} finally {
			super.finalize();
		}
	}

}




