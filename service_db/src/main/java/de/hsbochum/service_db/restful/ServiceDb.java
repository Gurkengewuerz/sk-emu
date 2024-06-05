package de.hsbochum.service_db.restful;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.hsbochum.service_db.restful.db.DbAktionen;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api")
public class ServiceDb {

    private static final Logger logger = Logger.getLogger(ServiceDb.class.getName());

    private final DbAktionen dbAktionen;
    private final ObjectMapper objectMapper;

    public ServiceDb() {
        this.dbAktionen = new DbAktionen();
        this.objectMapper = new ObjectMapper();
    }

    @GET
    @Produces("text/plain")
    public String hello() {
        return "Hello, World from DB!";
    }

    @GET
    @Path("/messreihen")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getMessreihen() throws SQLException, JsonProcessingException {
        String messreihenJSON = "";
        this.dbAktionen.connectDb();
        Messreihe[] messreihe = this.dbAktionen.leseMessreihenInklusiveMessungen();
        this.dbAktionen.closeDb();
        messreihenJSON = this.objectMapper.writeValueAsString(messreihe);
        return messreihenJSON;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/messreihe/{MessreihenId}/messungen")
    public String getMessungen(@PathParam("MessreihenId") String MessreihenId) throws SQLException, JsonProcessingException {
        String messungenJSON = "";
        this.dbAktionen.connectDb();
        int MessreihenIdInteger = Integer.parseInt(MessreihenId);
        Messung[] messungen = this.dbAktionen.leseMessungen(MessreihenIdInteger);
        this.dbAktionen.closeDb();
        messungenJSON = this.objectMapper.writeValueAsString(messungen);
        return messungenJSON;
    }

    @POST
    @Path("/messreihe/{MessreihenId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createMessreihe(Messreihe messreihe, @PathParam("MessreihenId") String MessreihenId) throws SQLException {
        this.dbAktionen.connectDb();
        try {
            this.dbAktionen.fuegeMessreiheEin(messreihe);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Fehler beim Erstellen einer Messreihe in createMessreihe()", e);
            return Response.status(Response.Status.CONFLICT).build();
        }
        this.dbAktionen.closeDb();
        String artikelEndpoint = "/api/db/messreihe/" + messreihe.getMessreihenId();
        System.out.println(artikelEndpoint);
        return Response.status(Response.Status.CREATED).entity(artikelEndpoint).build();
    }

    @POST
    @Path("/messung/{MessreihenId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createMessung(Messung messung, @PathParam("MessreihenId") String MessreihenId) throws SQLException {
        int messreihenIdInteger = Integer.parseInt(MessreihenId);
        this.dbAktionen.connectDb();
        try {
            Messreihe messreihe = dbAktionen.leseMessreihe(messreihenIdInteger);
            // Laden der existierenden Messungen zur Überprüfung des Zeitintervalls
            Messung[] vorhandeneMessungen = this.dbAktionen.leseMessungen(messreihenIdInteger);

            // Überprüfen des Zeitintervalls
            for (Messung vorhandeneMessung : vorhandeneMessungen) {
                if (messung.getTimeMillis() - vorhandeneMessung.getTimeMillis() < messreihe.getZeitintervall()) {
                    logger.log(Level.SEVERE, "Zeitintervall zwischen den Messungen zu kurz");
                    return Response.status(Response.Status.CONFLICT).build();
                }
            }

            // Speichern der neuen Messung
            this.dbAktionen.fuegeMessungEin(messreihenIdInteger, messung);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Fehler beim Erstellen einer Messung in createMessung()", e);
            return Response.status(Response.Status.CONFLICT).build();
        }
        this.dbAktionen.closeDb();
        String artikelEndpoint = "/api/db/messung/" + MessreihenId;
        return Response.status(Response.Status.CREATED).entity(artikelEndpoint).build();
    }

    @DELETE
    @Path("/messreihe/{MessreihenId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response loescheMessreiheInklusiveMessungen(@PathParam("MessreihenId") String MessreihenId) throws SQLException {
        int messreihenIdInteger = Integer.parseInt(MessreihenId);
        this.dbAktionen.connectDb();
        try {
            this.dbAktionen.loescheMessreiheInklusiveMessungen(messreihenIdInteger);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Fehler beim Löschen einer Messreihe in loescheMessreiheInklusiveMessungen()", e);
            return Response.status(Response.Status.CONFLICT).build();
        }
        this.dbAktionen.closeDb();
        String artikelEndpoint = "/api/db/messreihe/" + messreihenIdInteger;
        return Response.status(Response.Status.OK).entity(artikelEndpoint).build();
    }
}