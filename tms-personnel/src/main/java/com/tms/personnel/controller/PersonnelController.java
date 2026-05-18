package com.tms.personnel.controller;

import com.tms.personnel.dto.ImportResultDTO;
import com.tms.personnel.dto.PersonnelRequest;
import com.tms.personnel.dto.PersonnelResponse;
import com.tms.personnel.dto.PersonnelStats;
import com.tms.personnel.service.PersonnelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/personnel")
@RequiredArgsConstructor
public class PersonnelController {

    private final PersonnelService personnelService;

    @GetMapping
    public ResponseEntity<Page<PersonnelResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String typeRessource) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PersonnelResponse> result;
        if (search != null && !search.isBlank()) {
            result = personnelService.search(search, pageable);
        } else if (typeRessource != null && !typeRessource.isBlank()) {
            result = personnelService.findByTypeRessource(typeRessource, pageable);
        } else {
            result = personnelService.findAll(pageable);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonnelResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(personnelService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonnelResponse> create(@RequestBody PersonnelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personnelService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonnelResponse> update(@PathVariable Long id, @RequestBody PersonnelRequest request) {
        return ResponseEntity.ok(personnelService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        personnelService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<PersonnelStats> getStats() {
        return ResponseEntity.ok(personnelService.getStats());
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ImportResultDTO> importFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(personnelService.importFromFile(file));
    }
}
