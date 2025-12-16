package org.folio.config;

import org.folio.dao.audit.AuditOutboxEventLogDAO;
import org.folio.dao.audit.AuditOutboxEventLogPostgresDAO;
import org.folio.dao.organization.OrganizationDAO;
import org.folio.dao.organization.OrganizationPostgresDAO;
import org.folio.kafka.KafkaConfig;
import org.folio.service.OrganizationService;
import org.folio.service.audit.AuditEventProducer;
import org.folio.service.audit.AuditOutboxService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(KafkaConfiguration.class)
public class ApplicationConfig {

  @Bean
  public OrganizationService organizationService(OrganizationDAO organizationDAO, AuditOutboxService auditOutboxService) {
    return new OrganizationService(organizationDAO, auditOutboxService);
  }

  @Bean
  public OrganizationDAO organizationDAO() {
    return new OrganizationPostgresDAO();
  }

  @Bean
  public AuditOutboxEventLogDAO auditOutboxEventLogDAO() {
    return new AuditOutboxEventLogPostgresDAO();
  }

  @Bean
  public AuditEventProducer auditEventProducer(KafkaConfig kafkaConfig) {
    return new AuditEventProducer(kafkaConfig);
  }

  @Bean
  public AuditOutboxService auditOutboxService(AuditOutboxEventLogDAO auditOutboxEventLogDAO, AuditEventProducer producer) {
    return new AuditOutboxService(auditOutboxEventLogDAO, producer);
  }
}
