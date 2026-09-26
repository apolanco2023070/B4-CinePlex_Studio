package org.cineplex.system.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.cineplex.system.model.TicketData;
import org.cineplex.system.model.TicketInfo;
import org.cineplex.system.model.User;
import org.cineplex.system.repository.TicketRepository;
import org.cineplex.system.utils.AlertInformation;

/**
 * HU37: Como Gerente, quiero emitir un boleto de ingreso en tamaño ticket a
 * partir de una reserva para entregar al cliente su comprobante.
 * HU38: Como sistema, quiero cruzar la reserva con la información de la
 * película, función, sala y asiento para generar un boleto completo.
 *
 * @author informatica
 */
public class TicketController {

    // ==========================================
    // Componentes para la vista de Lista de Reservas
    // ==========================================
    @FXML
    private TableView<TicketInfo> tblReservas;

    @FXML
    private TableColumn<TicketInfo, String> colCliente;

    @FXML
    private TableColumn<TicketInfo, String> colPelicula;

    @FXML
    private TableColumn<TicketInfo, String> colSala;

    @FXML
    private TableColumn<TicketInfo, String> colFecha;

    @FXML
    private TableColumn<TicketInfo, String> colHora;

    @FXML
    private TableColumn<TicketInfo, Integer> colAsiento;

    @FXML
    private TableColumn<TicketInfo, String> colTieneBoleto;

    @FXML
    private Button btnEmitir;

    @FXML
    private Button btnRegresar;

    // ==========================================
    // Componentes para la vista individual del Ticket
    // ==========================================
    @FXML
    private Label lblTicketNumber;
    
    @FXML
    private Label lblMovie;
    
    @FXML
    private Label lblAuditorium;
    
    @FXML
    private Label lblDate;
    
    @FXML
    private Label lblTime;
    
    @FXML
    private Label lblSeat;
    
    @FXML
    private Label lblUser;
    
    @FXML
    private Label lblIssueDate;
    
    @FXML
    private Button btnClose;

    // ==========================================
    // Lógica del Controlador
    // ==========================================
    private final TicketRepository ticketRepository;
    private User usuarioLogueado;
    private TicketData ticketData;

    public TicketController() {
        this.ticketRepository = new TicketRepository();
    }

    public void setUsuarioLogueado(User usuario) {
        this.usuarioLogueado = usuario;
    }

    @FXML
    public void initialize() {
        // Solo configuramos la tabla si los campos existen en el FXML actual
        if (tblReservas != null) {
            configurarTabla();
            cargarReservas();
        }
    }

    private void configurarTabla() {
        colCliente.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colPelicula.setCellValueFactory(new PropertyValueFactory<>("movieTitle"));
        colSala.setCellValueFactory(new PropertyValueFactory<>("auditoriumName"));
        colFecha.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getShowDate() != null ? data.getValue().getShowDate().toString() : ""));
        colHora.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getShowTime() != null ? data.getValue().getShowTime().toString() : ""));
        colAsiento.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));
        colTieneBoleto.setCellValueFactory(new PropertyValueFactory<>("hasTicketLabel"));
    }

    private void cargarReservas() {
        try {
            List<TicketInfo> reservas = ticketRepository.getActiveReservations();
            tblReservas.setItems(FXCollections.observableArrayList(reservas));
        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Error", "No se pudieron cargar las reservas", e.getMessage());
        }
    }

    @FXML
    private void emitirBoleto() {
        TicketInfo seleccionada = tblReservas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            AlertInformation.viewAlert("WARNING", "Atención", "Sin selección", "Selecciona una reserva de la tabla.");
            return;
        }
        if (seleccionada.isHasTicket()) {
            AlertInformation.viewAlert("WARNING", "Atención", "Boleto ya emitido",
                    "Esta reserva ya tiene un boleto emitido.");
            return;
        }

        try {
            // HU37: registrar la emisión del boleto.
            ticketRepository.issueTicket(seleccionada.getReservationId());

            // HU38: cruzar la reserva con película, función, sala y asiento para el boleto completo.
            TicketInfo detalle = ticketRepository.getReservationDetails(seleccionada.getReservationId());

            mostrarBoleto(detalle);
            cargarReservas();

        } catch (Exception e) {
            AlertInformation.viewAlert("ERROR", "Error", "No se pudo emitir el boleto", e.getMessage());
        }
    }

    /**
     * Muestra el boleto en una ventana angosta, tamaño ticket, con opción de imprimir.
     */
    private void mostrarBoleto(TicketInfo info) {
        VBox boleto = new VBox(6);
        boleto.setAlignment(Pos.CENTER);
        boleto.setPadding(new Insets(16));
        boleto.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1;");

        Label lblCine = new Label("CINEPLEX STUDIO");
        lblCine.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Label lblPelicula = new Label(info.getMovieTitle());
        lblPelicula.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblPelicula.setWrapText(true);
        lblPelicula.setAlignment(Pos.CENTER);

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        VBox datos = new VBox(4);
        datos.setAlignment(Pos.CENTER_LEFT);
        datos.getChildren().addAll(
                new Label("Sala: " + info.getAuditoriumName()),
                new Label("Fecha: " + info.getShowDate().format(dateFmt)),
                new Label("Hora: " + info.getShowTime().format(timeFmt)),
                new Label("Asiento: " + info.getSeatNumber()),
                new Label("Duración: " + info.getDuration() + " min"),
                new Label("Cliente: " + info.getCustomerName())
        );

        Label lblFolio = new Label("Boleto #" + info.getReservationId());
        lblFolio.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        Button btnImprimir = new Button("Imprimir");
        Button btnCerrar = new Button("Cerrar");

        Stage stageBoleto = new Stage();
        stageBoleto.initModality(Modality.APPLICATION_MODAL);
        stageBoleto.setTitle("Boleto - CinePlex");
        stageBoleto.setResizable(false);

        btnImprimir.setOnAction(e -> imprimirNodo(boleto));
        btnCerrar.setOnAction(e -> stageBoleto.close());

        boleto.getChildren().addAll(
                lblCine,
                new Separator(),
                lblPelicula,
                datos,
                new Separator(),
                lblFolio,
                btnImprimir,
                btnCerrar
        );

        Scene escenaBoleto = new Scene(boleto, 280, 460);
        stageBoleto.setScene(escenaBoleto);
        stageBoleto.showAndWait();
    }

    private void imprimirNodo(Node nodo) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            AlertInformation.viewAlert("ERROR", "Error", "Impresión no disponible",
                    "No se encontró una impresora configurada en este equipo.");
            return;
        }
        boolean aceptado = job.showPrintDialog(nodo.getScene().getWindow());
        if (aceptado) {
            boolean impreso = job.printPage(nodo);
            if (impreso) {
                job.endJob();
            } else {
                AlertInformation.viewAlert("ERROR", "Error", "No se pudo imprimir", "La impresión falló.");
            }
        }
    }

@FXML
private void regresarMenu() {
    try {
        Stage stageActual = (Stage) btnRegresar.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/cineplex/system/view/Gerente.fxml"));
        Parent root = loader.load();
        GerenteController controller = loader.getController();
        
        // ✅ Validación: solo pasar el usuario si no es null
        if (this.usuarioLogueado != null) {
            controller.setUsuarioLogueado(this.usuarioLogueado);
        }
        
        Scene escenaNueva = new Scene(root);
        stageActual.setScene(escenaNueva);
        stageActual.show();
    } catch (Exception e) {
        e.printStackTrace();
        AlertInformation.viewAlert("ERROR", "Error de navegación", "No se pudo cargar la vista", e.getMessage());
    }
}

    public void initData(TicketData ticketData) {
        this.ticketData = ticketData;
        cargarDatosTicket();
    }

    private void cargarDatosTicket() {
        // Validación de seguridad por si se llama antes de que el FXML cargue
        if (lblTicketNumber != null && ticketData != null) {
            lblTicketNumber.setText("TICKET #" + String.format("%03d", ticketData.getTicketNumber()));
            lblMovie.setText(ticketData.getMovieTitle());
            lblAuditorium.setText(ticketData.getAuditoriumName());
            lblDate.setText(ticketData.getShowDate().toString());
            lblTime.setText(ticketData.getShowTime().toString());
            lblSeat.setText("Asiento " + ticketData.getSeatNumber());
            lblUser.setText(ticketData.getUserName());
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            lblIssueDate.setText(ticketData.getIssueDate().format(formatter));
        }
    }

    @FXML
    private void cerrarTicket() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}