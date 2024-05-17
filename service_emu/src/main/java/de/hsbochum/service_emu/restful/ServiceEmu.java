package de.hsbochum.service_emu.restful;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.hsbochum.service_emu.restful.emu.EmuAktionen;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api")
public class ServiceEmu {
    private final EmuAktionen emuAktionen;
    private final ObjectMapper objectMapper;

    public ServiceEmu() {
        this.emuAktionen = new EmuAktionen();
        this.objectMapper = new ObjectMapper();
    }

    @GET
    @Produces("text/plain")
    public String hello() {
        return "Hello, World from EMU!";
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/messung/{laufendeNummer}")
    public String getMessungen(@PathParam("laufendeNummer") int laufendeNummer) throws JsonProcessingException, RuntimeException {
        String messungenJSON = "";
        messungenJSON = this.objectMapper.writeValueAsString(this.emuAktionen.holeMessungVonEMU(laufendeNummer));
        return messungenJSON;
    }

}