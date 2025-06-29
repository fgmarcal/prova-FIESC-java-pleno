package org.fiesc.felipe.api.modules.interfaces.handler;

import java.time.LocalDateTime;

public record ErrorResponse(
        String mensagem,
        String detalhe,
        LocalDateTime timestamp,
        int status
) {}
