package cl.koritsu.valued.view.sales;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.vaadin.maddon.ListContainer;
import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import cl.koritsu.valued.ValuedUI;
import cl.koritsu.valued.domain.Movie;
import cl.koritsu.valued.domain.MovieRevenue;

@SuppressWarnings("serial")
public class SalesView extends VerticalLayout implements View, WizardProgressListener {

    private ComboBox movieSelect;
    
    private Wizard wizard;

    public SalesView() {
        setSizeFull();
        addStyleName("sales");

        addComponent(buildHeader());
        
        VerticalLayout vl = buildContent();
        addComponent(vl);
        setExpandRatio(vl, 1);
/*
        timeline = buildTimeline();
        addComponent(timeline);
        setExpandRatio(timeline, 1);

        initMovieSelect();
        // Add first 4 by default
        List<Movie> subList = new ArrayList<Movie>(ValuedUI
                .getDataProvider().getMovies()).subList(0, 4);
        for (Movie m : subList) {
            addDataSet(m);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -2);
        
        if (timeline.getGraphDatasources().size() > 0) {
            timeline.setVisibleDateRange(calendar.getTime(), new Date());
        }
        */
    }

    private VerticalLayout buildContent() {
    	
    	 wizard = new Wizard();
    	 wizard.setSizeFull();
    	 
         wizard.setUriFragmentEnabled(true);
         wizard.addListener(this);
         wizard.getBackButton().setCaption("Anterior");
         wizard.getNextButton().setCaption("Siguiente");
         wizard.getFinishButton().setCaption("Finalizar");
         wizard.getCancelButton().setCaption("Cancelar");
         
         wizard.addStep(new ClienteStep(), "cliente");
         wizard.addStep(new BienStep(), "bien");
         wizard.addStep(new SolTasacionStep(), "solicitud");
         wizard.addStep(new HonorarioClienteStep(wizard), "ingreso");
         wizard.setSizeFull();
		return new VerticalLayout(){
			{
				setSizeFull();
				addComponent(wizard);
			}
		};
	}

	private void initMovieSelect() {
        Collection<Movie> movies = ValuedUI.getDataProvider().getMovies();
        Container movieContainer = new ListContainer<Movie>(Movie.class, movies);
        movieSelect.setContainerDataSource(movieContainer);
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);
        Responsive.makeResponsive(header);

        Label titleLabel = new Label("Nueva Tasación");
        titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_H1);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(titleLabel);

        return header;
    }

    private Component buildToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.addStyleName("toolbar");
        toolbar.setSpacing(true);

        movieSelect = new ComboBox();
        movieSelect.setItemCaptionPropertyId("title");
        movieSelect.addShortcutListener(new ShortcutListener("Add",
                KeyCode.ENTER, null) {
            @Override
            public void handleAction(final Object sender, final Object target) {
                addDataSet((Movie) movieSelect.getValue());
            }
        });

        final Button add = new Button("Add");
        add.setEnabled(false);
        add.addStyleName(ValoTheme.BUTTON_PRIMARY);

        CssLayout group = new CssLayout(movieSelect, add);
        group.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        toolbar.addComponent(group);

        movieSelect.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(final ValueChangeEvent event) {
                add.setEnabled(event.getProperty().getValue() != null);
            }
        });

        final Button clear = new Button("Clear");
        clear.addStyleName("clearbutton");
        clear.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                //timeline.removeAllGraphDataSources();
                initMovieSelect();
                clear.setEnabled(false);
            }
        });
        toolbar.addComponent(clear);

        add.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                addDataSet((Movie) movieSelect.getValue());
                clear.setEnabled(true);
            }
        });

        return toolbar;
    }
/*
    private Timeline buildTimeline() {
        Timeline result = new Timeline();
        result.setDateSelectVisible(false);
        result.setChartModesVisible(false);
        result.setGraphShadowsEnabled(false);
        result.setZoomLevelsVisible(false);
        result.setSizeFull();
        result.setNoDataSourceCaption("<span class=\"v-label h2 light\">Add a data set from the dropdown above</span>");
        return result;
    }
*/
    private void addDataSet(final Movie movie) {
        movieSelect.removeItem(movie);
        movieSelect.setValue(null);

        Collection<MovieRevenue> dailyRevenue = ValuedUI.getDataProvider()
                .getDailyRevenuesByMovie(movie.getId());

        ListContainer<MovieRevenue> dailyRevenueContainer = new TempMovieRevenuesContainer(
                dailyRevenue);

        dailyRevenueContainer.sort(new Object[] { "timestamp" },
                new boolean[] { true });
/*
        timeline.addGraphDataSource(dailyRevenueContainer, "timestamp",
                "revenue");
        colorIndex = (colorIndex >= COLORS.length - 1 ? 0 : ++colorIndex);
        timeline.setGraphOutlineColor(dailyRevenueContainer, COLORS[colorIndex]);
        timeline.setBrowserOutlineColor(dailyRevenueContainer,
                COLORS[colorIndex]);
        timeline.setBrowserFillColor(dailyRevenueContainer,
                COLORS_ALPHA[colorIndex]);
        timeline.setGraphCaption(dailyRevenueContainer, movie.getTitle());
        timeline.setEventCaptionPropertyId("date");
        timeline.setVerticalAxisLegendUnit(dailyRevenueContainer, "$");
        */
    }

    @Override
    public void enter(final ViewChangeEvent event) {
    }

    private class TempMovieRevenuesContainer extends
            ListContainer<MovieRevenue> {

        public TempMovieRevenuesContainer(
                final Collection<MovieRevenue> collection) {
            super(MovieRevenue.class, collection);
        }

        // This is only temporarily overridden until issues with
        // BeanComparator get resolved.
        @Override
        public void sort(final Object[] propertyId, final boolean[] ascending) {
            final boolean sortAscending = ascending[0];
            Collections.sort(getBackingList(), new Comparator<MovieRevenue>() {
                @Override
                public int compare(final MovieRevenue o1, final MovieRevenue o2) {
                    int result = o1.getTimestamp().compareTo(o2.getTimestamp());
                    if (!sortAscending) {
                        result *= -1;
                    }
                    return result;
                }
            });
        }

    }

	@Override
	public void activeStepChanged(WizardStepActivationEvent event) {
		Page.getCurrent().setTitle(event.getActivatedStep().getCaption());
	}

	@Override
	public void stepSetChanged(WizardStepSetChangedEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wizardCompleted(WizardCompletedEvent event) {
		endWizard("Wizard Completed!");
	}

	@Override
	public void wizardCancelled(WizardCancelledEvent event) {
		endWizard("Wizard Cancelled!");
	}
	
    private void endWizard(String message) {
        wizard.setVisible(false);
        Notification.show(message);
        Page.getCurrent().setTitle(message);
        Button startOverButton = new Button("Run the demo again",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        // Close the session and reload the page.
                        VaadinSession.getCurrent().close();
                        Page.getCurrent().setLocation("");
                    }
                });
        addComponent(startOverButton);
        setComponentAlignment(startOverButton,
                Alignment.MIDDLE_CENTER);
    }
}
