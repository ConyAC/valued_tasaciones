package cl.koritsu.valued.view.dashboard;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.google.common.eventbus.Subscribe;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import cl.koritsu.valued.ValuedUI;
import cl.koritsu.valued.component.SparklineChart;
import cl.koritsu.valued.domain.ConsolidadoCliente;
import cl.koritsu.valued.domain.DashboardNotification;
import cl.koritsu.valued.event.ValuedEvent.CloseOpenWindowsEvent;
import cl.koritsu.valued.event.ValuedEvent.NotificationsCountUpdatedEvent;
import cl.koritsu.valued.event.ValuedEventBus;
import cl.koritsu.valued.services.ValuedService;
import cl.koritsu.valued.view.dashboard.DashboardEdit.DashboardEditListener;
import ru.xpoft.vaadin.VaadinView;

@SuppressWarnings("serial")
@org.springframework.stereotype.Component
@Scope("prototype")
@VaadinView(value = DashboardView.NAME, cached = true)
public final class DashboardView extends Panel implements View,
        DashboardEditListener {
	
	public static final String NAME = "dashboard";

    public static final String EDIT_ID = "dashboard-edit";
    public static final String TITLE_ID = "dashboard-title";

    private Label titleLabel;
    private CssLayout dashboardPanels;
    private VerticalLayout root;
    private Window notificationsWindow;
    
    @Autowired
    ValuedService service;
    
    public DashboardView() {
    }
    
    @PostConstruct
    public void init() {
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();
        ValuedEventBus.register(this);

        root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        root.addStyleName("dashboard-view");
        setContent(root);
        Responsive.makeResponsive(root);

        root.addComponent(buildHeader());

        root.addComponent(buildSparklines());

        Component content = buildContent();
        root.addComponent(content);
        root.setExpandRatio(content, 1);

        // All the open sub-windows should be closed whenever the root layout
        // gets clicked.
        root.addLayoutClickListener(new LayoutClickListener() {
            @Override
            public void layoutClick(final LayoutClickEvent event) {
                ValuedEventBus.post(new CloseOpenWindowsEvent());
            }
        });
    }

    private Component buildSparklines() {
        CssLayout sparks = new CssLayout();
        sparks.addStyleName("sparks");
        sparks.setWidth("100%");
        Responsive.makeResponsive(sparks);
        
        int tasacionesSinAsignar = service.countTasacionesSinAsignar();
        int tasacionesSinTasar = service.countTasacionesSinTasar();
        int tasacionesSinVisar = service.countTasacionesSinVisar();
        int tasacionesSinFacturar = service.countTasacionesSinFacturar();
        int tasacionesFacturadas = service.countMontoFacturoPorMes(new Date());

        SparklineChart s = new SparklineChart("Sin Asignar", " tas.", "", tasacionesSinAsignar );
        sparks.addComponent(s);
/*
        s = new SparklineChart("Sin Tasar", " tas.", "", tasacionesSinTasar );
        sparks.addComponent(s);
*/
        s = new SparklineChart("Sin Visar", " tas.", "", tasacionesSinVisar );
        sparks.addComponent(s);

        s = new SparklineChart("Sin Facturar", " tas.", "", tasacionesSinFacturar );
        sparks.addComponent(s);
        
        s = new SparklineChart("Facturado este mes", "", "$ ", tasacionesFacturadas );
        sparks.addComponent(s);

        return sparks;
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);

        titleLabel = new Label("Dashboard");
        titleLabel.setId(TITLE_ID);
        titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_H1);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(titleLabel);

        return header;
    }

    private Component buildContent() {
        dashboardPanels = new CssLayout();
        dashboardPanels.addStyleName("dashboard-panels");
        Responsive.makeResponsive(dashboardPanels);

        dashboardPanels.addComponent(buildTop10TasacionesSinVisar());
        dashboardPanels.addComponent(buildNotes());
        dashboardPanels.addComponent(buildTop10TasacionesPorClienteMes());
        dashboardPanels.addComponent(buildTop10Facturaciones());

        return dashboardPanels;
    }

    private Component buildTop10TasacionesSinVisar() {
        VerticalLayout topGrossingMoviesChart = new VerticalLayout();
        topGrossingMoviesChart.setCaption("Cantidad tasaciones sin visar por cliente");
        topGrossingMoviesChart.setSizeFull();
        
        List<ConsolidadoCliente> sinVisar = service.top10TasacionesSinVisarByCliente();
        
        Table table = new Table();
        BeanItemContainer<ConsolidadoCliente> btc = new BeanItemContainer<ConsolidadoCliente>(ConsolidadoCliente.class,sinVisar);
        btc.addNestedContainerBean("cliente");
        table.setContainerDataSource(btc);
        table.setVisibleColumns("cliente.nombreCliente","cantidad");
        table.setColumnHeaders("Cliente","Cantidad");
        table.setSizeFull();
        
        topGrossingMoviesChart.addComponent(table);
        
        return createContentWrapper(topGrossingMoviesChart);
    }
    
    private Component buildTop10TasacionesPorClienteMes() {
        VerticalLayout topGrossingMoviesChart = new VerticalLayout();
        topGrossingMoviesChart.setCaption("Cantidad tasaciones encargadas por cliente en el mes");
        topGrossingMoviesChart.setSizeFull();
        
        List<ConsolidadoCliente> cantTasacionesCliente = service.top10TasacionesMesByCliente(new Date());
        
        Table table = new Table();
        BeanItemContainer<ConsolidadoCliente> btc = new BeanItemContainer<ConsolidadoCliente>(ConsolidadoCliente.class,cantTasacionesCliente);
        btc.addNestedContainerBean("cliente");
        table.setContainerDataSource(btc);
        table.setVisibleColumns("cliente.nombreCliente","cantidad");
        table.setColumnHeaders("Cliente","Cantidad");
        table.setSizeFull();
        
        topGrossingMoviesChart.addComponent(table);
        
        
        return createContentWrapper(topGrossingMoviesChart);
    }
    
    
    private Component buildTop10Facturaciones() {
        VerticalLayout topGrossingMoviesChart = new VerticalLayout();
        
        topGrossingMoviesChart.setCaption("Facturacion por cliente en el mes");
        topGrossingMoviesChart.setSizeFull();
        
        List<ConsolidadoCliente> cantTasacionesCliente = service.top10FacturacionMesByCliente(new Date());
        
        Table table = new Table();
        BeanItemContainer<ConsolidadoCliente> btc = new BeanItemContainer<ConsolidadoCliente>(ConsolidadoCliente.class,cantTasacionesCliente);
        btc.addNestedContainerBean("cliente");
        table.setContainerDataSource(btc);
        table.setVisibleColumns("cliente.nombreCliente","cantidad");
        table.setColumnHeaders("Cliente","Cantidad");
        table.setSizeFull();
        
        topGrossingMoviesChart.addComponent(table);
        
        return createContentWrapper(topGrossingMoviesChart);
    }

    private Component buildNotes() {
        TextArea notes = new TextArea("Notas");
        notes.setValue("Esta semana deben considerar to:\n· Se esperan 5 tasaciones la proxima semana de itau\n· La Fran tienen pendiente 6 visaciones");
        notes.setSizeFull();
        notes.addStyleName(ValoTheme.TEXTAREA_BORDERLESS);
        Component panel = createContentWrapper(notes);
        panel.addStyleName("notes");
        return panel;
    }

    private Component buildPopularMovies() {
        return createContentWrapper(
        		//new TopSixTheatersChart()
        		new VerticalLayout()
        		);
    }

    private Component createContentWrapper(final Component content) {
        final CssLayout slot = new CssLayout();
        slot.setWidth("100%");
        slot.addStyleName("dashboard-panel-slot");

        CssLayout card = new CssLayout();
        card.setWidth("100%");
        card.addStyleName(ValoTheme.LAYOUT_CARD);

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.addStyleName("dashboard-panel-toolbar");
        toolbar.setWidth("100%");

        Label caption = new Label(content.getCaption());
        caption.addStyleName(ValoTheme.LABEL_H4);
        caption.addStyleName(ValoTheme.LABEL_COLORED);
        caption.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        content.setCaption(null);

        MenuBar tools = new MenuBar();
        tools.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
        MenuItem max = tools.addItem("", FontAwesome.EXPAND, new Command() {

            @Override
            public void menuSelected(final MenuItem selectedItem) {
                if (!slot.getStyleName().contains("max")) {
                    selectedItem.setIcon(FontAwesome.COMPRESS);
                    toggleMaximized(slot, true);
                } else {
                    slot.removeStyleName("max");
                    selectedItem.setIcon(FontAwesome.EXPAND);
                    toggleMaximized(slot, false);
                }
            }
        });
        max.setStyleName("icon-only");
        MenuItem root = tools.addItem("", FontAwesome.COG, null);
        root.addItem("Config", new Command() {
            @Override
            public void menuSelected(final MenuItem selectedItem) {
                Notification.show("No implementado");
            }
        });
        root.addSeparator();
        root.addItem("Cerrar", new Command() {
            @Override
            public void menuSelected(final MenuItem selectedItem) {
                Notification.show("No implementado");
            }
        });

        toolbar.addComponents(caption, tools);
        toolbar.setExpandRatio(caption, 1);
        toolbar.setComponentAlignment(caption, Alignment.MIDDLE_LEFT);

        card.addComponents(toolbar, content);
        slot.addComponent(card);
        return slot;
    }

    private void openNotificationsPopup(final ClickEvent event) {
        VerticalLayout notificationsLayout = new VerticalLayout();
        notificationsLayout.setMargin(true);
        notificationsLayout.setSpacing(true);

        Label title = new Label("Notifications");
        title.addStyleName(ValoTheme.LABEL_H3);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        notificationsLayout.addComponent(title);

        Collection<DashboardNotification> notifications = ValuedUI
                .getDataProvider().getNotifications();
        ValuedEventBus.post(new NotificationsCountUpdatedEvent());

        for (DashboardNotification notification : notifications) {
            VerticalLayout notificationLayout = new VerticalLayout();
            notificationLayout.addStyleName("notification-item");

            Label titleLabel = new Label(notification.getFirstName() + " "
                    + notification.getLastName() + " "
                    + notification.getAction());
            titleLabel.addStyleName("notification-title");

            Label timeLabel = new Label(notification.getPrettyTime());
            timeLabel.addStyleName("notification-time");

            Label contentLabel = new Label(notification.getContent());
            contentLabel.addStyleName("notification-content");

            notificationLayout.addComponents(titleLabel, timeLabel,
                    contentLabel);
            notificationsLayout.addComponent(notificationLayout);
        }

        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth("100%");
        Button showAll = new Button("View All Notifications",
                new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
                        Notification.show("Not implemented in this demo");
                    }
                });
        showAll.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        showAll.addStyleName(ValoTheme.BUTTON_SMALL);
        footer.addComponent(showAll);
        footer.setComponentAlignment(showAll, Alignment.TOP_CENTER);
        notificationsLayout.addComponent(footer);

        if (notificationsWindow == null) {
            notificationsWindow = new Window();
            notificationsWindow.setWidth(300.0f, Unit.PIXELS);
            notificationsWindow.addStyleName("notifications");
            notificationsWindow.setClosable(false);
            notificationsWindow.setResizable(false);
            notificationsWindow.setDraggable(false);
            notificationsWindow.setCloseShortcut(KeyCode.ESCAPE, null);
            notificationsWindow.setContent(notificationsLayout);
        }

        if (!notificationsWindow.isAttached()) {
            notificationsWindow.setPositionY(event.getClientY()
                    - event.getRelativeY() + 40);
            getUI().addWindow(notificationsWindow);
            notificationsWindow.focus();
        } else {
            notificationsWindow.close();
        }
    }

    @Override
    public void enter(final ViewChangeEvent event) {
    }

    @Override
    public void dashboardNameEdited(final String name) {
        titleLabel.setValue(name);
    }

    private void toggleMaximized(final Component panel, final boolean maximized) {
        for (Iterator<Component> it = root.iterator(); it.hasNext();) {
            it.next().setVisible(!maximized);
        }
        dashboardPanels.setVisible(true);

        for (Iterator<Component> it = dashboardPanels.iterator(); it.hasNext();) {
            Component c = it.next();
            c.setVisible(!maximized);
        }

        if (maximized) {
            panel.setVisible(true);
            panel.addStyleName("max");
        } else {
            panel.removeStyleName("max");
        }
    }

    public static final class NotificationsButton extends Button {
        private static final String STYLE_UNREAD = "unread";
        public static final String ID = "dashboard-notifications";

        public NotificationsButton() {
            setIcon(FontAwesome.BELL);
            setId(ID);
            addStyleName("notifications");
            addStyleName(ValoTheme.BUTTON_ICON_ONLY);
            ValuedEventBus.register(this);
        }

        @Subscribe
        public void updateNotificationsCount(
                final NotificationsCountUpdatedEvent event) {
            setUnreadCount(ValuedUI.getDataProvider()
                    .getUnreadNotificationsCount());
        }

        public void setUnreadCount(final int count) {
            setCaption(String.valueOf(count));

            String description = "Notifications";
            if (count > 0) {
                addStyleName(STYLE_UNREAD);
                description += " (" + count + " unread)";
            } else {
                removeStyleName(STYLE_UNREAD);
            }
            setDescription(description);
        }
    }

}
