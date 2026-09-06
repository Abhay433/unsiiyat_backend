package com.unsiiyat.backend.modules.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unsiiyat.backend.common.response.ApiResponse;
import com.unsiiyat.backend.common.response.PagedResponse;

@RestController
@RequestMapping("/api/scripts")
public class ScriptController {

    @Autowired
    private ScriptService scriptService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptController.class);

    @PostMapping("/list")
    public ResponseEntity<PagedResponse<ScriptDto>> filterScript(@RequestBody ScriptFilterRequest request) {
        LOGGER.debug("filterScript endpoint called");
        PagedResponse<ScriptDto> response = scriptService.filterScript(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/addOrUpdate")
    public ResponseEntity<ApiResponse<Void>> addOrUpdateScript(@RequestBody ScriptDto request) {
        LOGGER.debug("addOrUpdateScript endpoint called");
        scriptService.addOrUpdateScript(request);
        return ResponseEntity.ok(ApiResponse.success("Script saved successfully", null));
    }

}
