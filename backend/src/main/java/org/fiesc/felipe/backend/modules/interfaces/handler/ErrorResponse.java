package org.fiesc.felipe.backend.modules.interfaces.handler;

import java.time.LocalDateTime;

public record ErrorResponse(
        String mensagem,
        String detalhe,
        LocalDateTime timestamp,
        int status
) {}
