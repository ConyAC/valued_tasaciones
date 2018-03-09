package cl.koritsu.valued.services;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import cl.koritsu.valued.domain.SolicitudTasacion;
import cl.koritsu.valued.domain.Usuario;


@Service
public class MailService {

	Logger logger = LoggerFactory.getLogger(MailService.class);

	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private VelocityEngine velocityEngine;
	@Autowired
	private ValuedService service;
	@Autowired
	private TaskExecutor taskExecutor;
	
	boolean debugmode = false;


	/**
	 * Enviar un a todos los usuarios con permiso confirmación central avisando que el 
	 * administrador de obra confirmó la asistencia del mes
	 */
	public void enviarAlertaVisitaVencida(SolicitudTasacion sol){

		if(debugmode)return;
		//realiza las llamadas asincronicamente
		taskExecutor.execute(new Runnable() {

			@Override
			public void run() {
				final Usuario usuario = service.findUsuarioByUsername(sol.getUsuario().getEmail());

				MimeMessagePreparator preparator = new MimeMessagePreparator() {
					public void prepare(MimeMessage mimeMessage) throws Exception {
						MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
						message.setSubject("Alerta Visita Pendiente");
						message.setTo(usuario.getEmail());
						message.setFrom("noreply@valued.cl");
						Map model = new HashMap();
						model.put("usuario", usuario);
						model.put("solicitud", sol);
						String text = VelocityEngineUtils.mergeTemplateIntoString(
								velocityEngine, "templates/mail/alerta_visita_pendiente_email.vm","UTF-8", model);
						message.setText(text, true);
					}
				};
				mailSender.send(preparator);
			}
		});



	}
}
