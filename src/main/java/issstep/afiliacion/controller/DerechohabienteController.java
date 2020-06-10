package issstep.afiliacion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import issstep.afiliacion.cons.DerechohabienteCONST;
import issstep.afiliacion.model.Curp;
import issstep.afiliacion.model.DatoABuscar;
import issstep.afiliacion.model.Derechohabiente;
import issstep.afiliacion.model.Email;
import issstep.afiliacion.model.InfoPersona;
import issstep.afiliacion.model.ResetPassword;
import issstep.afiliacion.model.ActualizarPassword;
import issstep.afiliacion.model.Beneficiario;
import issstep.afiliacion.service.ArchivoService;
import issstep.afiliacion.service.DerechohabienteService;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/derechohabiente")
public class DerechohabienteController {


    @Autowired
    public DerechohabienteService derechohabienteService;
    
    @Autowired
    public ArchivoService archivoService;
    
    // servicio para la validacion por curp de los derechohabientes
    @JsonView(Derechohabiente.Views.Simple.class)
    @ApiOperation(value = "Verifica si hay un derechohabiente mediante su RFC")
    @RequestMapping(value = "/validaRfc", method = RequestMethod.POST)
    public ResponseEntity<?> validaPersonaCurp( @ApiParam(value = DerechohabienteCONST.curp, required = true)@RequestBody Curp curp, HttpServletResponse response) {

    	return derechohabienteService.getPersonaByCurp(curp.getRfc());
    }
    
    @JsonView(Derechohabiente.Views.Simple.class)
    @ApiOperation(value = "Verifica si hay un derechohabiente mediante su numero de afiliacion")
    @RequestMapping(value = "/validaNoAfiliacion/{noAfiliacion}", method = RequestMethod.GET)
    public ResponseEntity<?> validaPersonaNoAfiliacion( @ApiParam(value = "noAfiliacion", required = true) @PathVariable long noAfiliacion, HttpServletResponse response) {

    	return derechohabienteService.validaPersonaNoAfiliacion(noAfiliacion);
    }
    
    @JsonView(Derechohabiente.Views.Simple.class)
    @ApiOperation(value = "Verifica si hay un derechohabiente mediante su nombre")
    @RequestMapping(value = "/validaNombre", method = RequestMethod.POST)
    public ResponseEntity<?> validaPersonaNombre( @ApiParam(value = DerechohabienteCONST.buscaDerechohabiente, required = true)  @RequestBody Derechohabiente persona, HttpServletResponse response) {

    	return derechohabienteService.getPersonaByNombre(persona, response);
    }
    
    /*
     * 
     * Aqui es el metodo para recuperar la informacion de un trabajador o beneficiario
     * 
     */
    @ApiOperation(value = "Obtiene la información de un preafiliado")
    @JsonView(Derechohabiente.Views.Simple.class)
    @RequestMapping(value = "/informacion", method = RequestMethod.PUT)
    public ResponseEntity<?> getPersonaById(@ApiParam(value = DerechohabienteCONST.informacionBeneficiario, required = true) @RequestBody InfoPersona infoPersona,
    										HttpServletResponse response) {

    	return derechohabienteService.getPersonaByNoControlNoPreafiliacion(infoPersona);
    }
    
    @ApiOperation(value = "Obtiene la información de un afiliado")
    @JsonView(Derechohabiente.Views.Simple.class)
    @RequestMapping(value = "/afiliado/{noControl}/{noAfiliacion}/{claveParentesco}", method = RequestMethod.GET)
    public ResponseEntity<?> getAfiliadoById(@ApiParam(value = "noControl", required = true) @PathVariable long noControl, 
    										@ApiParam(value = "noAfiliacion", required = true) @PathVariable long noAfiliacion,
    										@ApiParam(value = "claveParentesco", required = true) @PathVariable long claveParentesco,
    										HttpServletResponse response) {

    	return derechohabienteService.getAfiliadoByNoControlNoPreafiliacion(noControl, noAfiliacion, claveParentesco);
    }
    
    
    @ApiOperation(value = "Relación de beneficiarios de un derechohabiente")
    @JsonView(Derechohabiente.Views.Simple.class)
    @RequestMapping(value = "/beneficiarios/{noControl}", method = RequestMethod.GET)
    public ResponseEntity<?> getBeneficiarios(@PathVariable("noControl") long noControl, HttpServletResponse response) {
    	
    	return derechohabienteService.getBeneficiarios(false, noControl);
    }
    
    @ApiOperation(value = "Relación de un derechohabiente y de sus beneficiarios ")
    @JsonView(Derechohabiente.Views.Simple.class)
    @RequestMapping(value = "/beneficiariosConTitular/{noControl}", method = RequestMethod.GET)
    public ResponseEntity<?> getBeneficiariosConTitular(@PathVariable("noControl") long noControl, HttpServletResponse response) {
    	
    	return derechohabienteService.getBeneficiarios(true, noControl);
    }
    
    
    @ApiOperation(value = "Registro de Trabajadores en linea")
    @JsonView(Derechohabiente.Views.RegistroDerechohabiente.class)
    @RequestMapping(value = "/registro/online", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registroOnline(@ApiParam(value = DerechohabienteCONST.registroUsuario, required = true) @RequestBody Derechohabiente persona) {
 
    	return derechohabienteService.registraUsuario(true, persona, 0);
    }
    
    @RequestMapping(value="/activar/{token}", method=RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> activarRegistro(@ApiParam(value = "Token", required = true) @PathVariable String token){

    	return derechohabienteService.activarRegistro(token);
	}
    
    @ApiOperation(value = "Registro de derechohabiente")
    @JsonView(Derechohabiente.Views.RegistroDerechohabiente.class)
    @RequestMapping(value = "/registro", method = RequestMethod.POST)
    public ResponseEntity<?> registraDerechohabiente(@ApiParam(value = DerechohabienteCONST.registroDerechohabiente, required = true)@RequestBody Derechohabiente registroDerechohabiente,
    												 HttpServletResponse response) {
   
    	return derechohabienteService.registraDerechohabiente(registroDerechohabiente);
    }
    
    @ApiOperation(value = "Asignar beneficiario")
    @JsonView(Derechohabiente.Views.RegistroDerechohabiente.class)
    @RequestMapping(value = "/asignar/beneficiario", method = RequestMethod.POST)
    public ResponseEntity<?> asignarBeneficiario(@ApiParam(value = DerechohabienteCONST.informacionBeneficiario, required = true)@RequestBody Beneficiario beneficiario,
    											 HttpServletResponse response) {
   
    	return derechohabienteService.asignarBeneficiario( beneficiario );
    }
    
    @ApiOperation(value = "Eliminar beneficiario")
    @JsonView(Derechohabiente.Views.RegistroDerechohabiente.class)
    @RequestMapping(value = "/eliminar/beneficiario/{idBeneficiario}", method = RequestMethod.DELETE)
    public ResponseEntity<?> eliminarBeneficiario(@ApiParam(value = "idBeneficiario", required = true)@PathVariable long idBeneficiario,
    											 HttpServletResponse response) {
   
    	return derechohabienteService.eliminarBeneficiario( idBeneficiario );
    }
    
    @ApiOperation(value = "Resetear password")
    @RequestMapping(value = "/recuperar/password", method=RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> recuperarPassword(@ApiParam(value = DerechohabienteCONST.recuperarPassword, required = true)@RequestBody ResetPassword resetPassword, HttpServletResponse response ){

    	return derechohabienteService.recuperarPassword(resetPassword);
	}
    
    @ApiOperation(value = "Solicitud para resetear password")
    @RequestMapping(value = "/solicitud/recuperar/password", method=RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> recuperarPassword( @ApiParam(value = DerechohabienteCONST.email, required = true)@RequestBody Email email, HttpServletResponse response ){

    	return derechohabienteService.solicitudRecuperarPassword(email.getEmail());
	}
    
    @ApiOperation(value = "Actualizar password")
    @RequestMapping(value = "/actualizar/password", method=RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> actualizarPassword( @ApiParam(value = DerechohabienteCONST.actualizarPassword, required = true)@RequestBody ActualizarPassword actualizarPassword, HttpServletResponse response ){

    	return derechohabienteService.actualizarPassword(actualizarPassword);
	}
    
    @ApiOperation(value = "Actualizar datos")
    @RequestMapping(value = "/actualizar/datos", method=RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> actualizarDatos( @ApiParam(value = DerechohabienteCONST.registroDerechohabiente, required = true)@RequestBody Derechohabiente datosDerechohabiente, HttpServletResponse response ){

    	return derechohabienteService.actualizarDatos(datosDerechohabiente);
	}
    
    
    /*
     * 
     * Aqui es el metodo para sacar el listado de los trabajadores.
     * 
     */
    @ApiOperation(value = "Relación de derechohabientes por estatus de validación")
    @RequestMapping(value = "/relacion/{estatusValidacion}", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getListaDerechohabientesPorEstatusDeValidacion(@ApiParam(value = "2 - Por validar, \n 1 - Valido, \n  0 - Invalido", required = true)
	 																		@PathVariable("estatusValidacion") int estatusValidacion,  
	 																		HttpServletResponse response ){

    	return derechohabienteService.getDerechohabientesPorEstatusDeValidacion(estatusValidacion);
	}
    
    /*
     * 
     * Aqui es el metodo para sacar el listado de los trabajadores a validar su documentacion.
     * 
     */
    @ApiOperation(value = "Relación de derechohabientes benefiiarios a validar documentacion")
    @RequestMapping(value = "/documentos_por_validar", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getListaDerechohabientesAndBeneficiariosPorValidarDoc(HttpServletResponse response ){

    	return derechohabienteService.getDerechohabientesAndBeneficiariosPorValidarODoctosValidados(false);
	}
    
    /*
     * 
     * Aqui es el metodo para sacar el listado de los trabajadores con doctos validados
     * 
     */
    @ApiOperation(value = "Relación de derechohabientes benefiiarios a validar documentacion")
    @RequestMapping(value = "/documentos_validados", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getListaDerechohabientesAndBeneficiariosConDoctosValidos(HttpServletResponse response ){

    	return derechohabienteService.getDerechohabientesAndBeneficiariosPorValidarODoctosValidados(true);
	}
    
    /*
     * 
     * Aqui es el metodo para sacar el listado de los trabajadores por notificar
     * 
     */
    @ApiOperation(value = "Relación de derechohabientes y benefiiarios por notificar con no afiliacion")
    @RequestMapping(value = "/por_notificar", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getListaDerechohabientesAndBeneficiariosPorNotificar(HttpServletResponse response ){

    	return derechohabienteService.getDerechohabientesAndBeneficiariosConNoAfiliacioPorNotificar(false);
	}
    
    /*
     * 
     * Aqui es el metodo para sacar el listado de los trabajadores notificados
     * 
     */
    @ApiOperation(value = "Relación de derechohabientes benefiiarios notificados")
    @RequestMapping(value = "/notificados", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getListaDerechohabientesAndBeneficiariosNotificados(HttpServletResponse response ){

    	return derechohabienteService.getDerechohabientesAndBeneficiariosConNoAfiliacioPorNotificar(true);
	}
    
    
    /*
     * 
     * Aqui es el metodo para sacar el listado de los trabajadores a notificar
     * 
     */
    @ApiOperation(value = "Relación de derechohabientes por estatus de validación")
    @RequestMapping(value = "/notificarDerechohabientes", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> notificarDerechohabientes(HttpServletResponse response ){

    	return derechohabienteService.notificarDerechohabientes();
	}
    
    @ApiOperation(value = "Buscar preafiliado")
    @RequestMapping(value = "/buscar/preafiliado", method=RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> buscarPreafiliado( @ApiParam(value = "{ \n \"dato\" : \"información a buscar\" \n}", required = true)@RequestBody DatoABuscar datoABuscar,
													HttpServletResponse response ){

    	return derechohabienteService.buscarInformacionEnPreafiliacion(true, datoABuscar, false);
	}
    
    @ApiOperation(value = "Buscar afiliado")
    @RequestMapping(value = "/buscar/afiliado", method=RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> buscarAfiliado( @ApiParam(value = "{ \n \"dato\" : \"información a buscar\" \n}", required = true)@RequestBody DatoABuscar datoABuscar,
													HttpServletResponse response ){

    	return derechohabienteService.buscarInformacionEnPreafiliacion(false, datoABuscar, false);
	}
    
    @ApiOperation(value = "Buscar como afiliado o beneficario")
    @RequestMapping(value = "/buscar/afiliadoOBeneficario", method=RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> afiliadoOBeneficario( @ApiParam(value = "{ \n \"dato\" : \"información a buscar\" \n}", required = true)@RequestBody DatoABuscar datoABuscar,
													HttpServletResponse response ){

    	return derechohabienteService.buscarInformacionEnPreafiliacion(false, datoABuscar, true);
	}
    
    @ApiOperation(value = "Documentacion faltante de los beneficiarios")
    @RequestMapping(value = "/documentacion/{noControl}", method = RequestMethod.GET)
    public ResponseEntity<?> getDocumentacionBeneficiarios(@PathVariable("noControl") long noControl, HttpServletResponse response) {
    	
    	return derechohabienteService.getDocumentacionBeneficiarios(false, noControl);
    }
    
    @ApiOperation(value = "Documentacion faltante de los beneficiarios y el titular")
    @RequestMapping(value = "/documentacionConTitular/{noControl}", method = RequestMethod.GET)
    public ResponseEntity<?> getDocumentacionBeneficiariosYTitular(@PathVariable("noControl") long noControl, HttpServletResponse response) {
    	
    	return derechohabienteService.getDocumentacionBeneficiarios(true, noControl);
    }
    
    /*
     * 
     * Aqui es el metodo para mandar a preafiliar un trabajador o beneficiario
     * 
     */
    
    @ApiOperation(value = "Derechohabiente listo para afiliar")
    @JsonView(Derechohabiente.Views.Simple.class)
    @RequestMapping(value = "/afiliar/{noControl}/{noPreAfiliacion}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateEstatusByNoControlAndNoPreafiliacion(
    										 @ApiParam(value = "noControl", required = true) @PathVariable long noControl, 
    										 @ApiParam(value = "noPreAfiliacion", required = true) @PathVariable long noPreAfiliacion,
    										 HttpServletResponse response) {

    	return derechohabienteService.updateEstatusByNoControlAndNoPreAfiliacion( noControl, noPreAfiliacion, 9 );
    }
    
    @ApiOperation(value = "Documentos de Derechohabiente listo para validar")
    @JsonView(Derechohabiente.Views.Simple.class)
    @RequestMapping(value = "/validarDocumentos/{noControl}/{noPreAfiliacion}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateEstatusDoctoByNoControlAndNoPreafiliacion(
    										 @ApiParam(value = "noControl", required = true) @PathVariable long noControl, 
    										 @ApiParam(value = "noPreAfiliacion", required = true) @PathVariable long noPreAfiliacion,
    										 HttpServletResponse response) {

    	return derechohabienteService.updateEstatusDoctosByNoControlAndNoPreAfiliacion( noControl, noPreAfiliacion, 2 );
    }
    
    
    @ApiOperation(value = "Documento de Afiliacion")
    @JsonView(Derechohabiente.Views.Simple.class)
    @RequestMapping(value = "/downloadAfiliacion/{noControl}/{noPreAfiliacion}", method = RequestMethod.GET)
    public ResponseEntity<?> updateSituacionAndNoAfiliacion(
											    		 @ApiParam(value = "noControl", required = true) @PathVariable long noControl, 
														 @ApiParam(value = "noPreAfiliacion", required = true) @PathVariable long noPreAfiliacion,
														 HttpServletResponse response) {

    	return archivoService.dowloadAfiliacionDerechohabiente( noControl , noPreAfiliacion, response );
    }
    
    @ApiOperation(value = "Verifica Afiliacion")
    @JsonView(Derechohabiente.Views.Simple.class)
    @RequestMapping(value = "/validaQR/{noAfiliacion}", method = RequestMethod.GET)
    public ResponseEntity<?> validarQR(
    										 @ApiParam(value = "noAfiliacion", required = true) @PathVariable long noAfiliacion,
    										 HttpServletResponse response) {

    	return archivoService.verificaAfiliacionDerechohabiente( noAfiliacion , response );
    }
    
}