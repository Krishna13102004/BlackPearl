package com.blackpearl.service;

import com.blackpearl.dto.TenderDto;
import com.blackpearl.exception.ResourceNotFoundException;
import com.blackpearl.model.Tender;
import com.blackpearl.repository.TenderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TenderService {

    private final TenderRepository tenderRepository;

    public List<TenderDto> getAllTenders() {
        if (com.blackpearl.security.SecurityUtils.isAdmin()) {
            return tenderRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }
        return getOpenTenders();
    }

    public List<TenderDto> getOpenTenders() {
        return tenderRepository.findByStatus(Tender.Status.OPEN).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TenderDto getTenderById(Long id) {
        return tenderRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Tender not found with id: " + id));
    }

    @Transactional
    public TenderDto createTender(TenderDto dto) {
        Tender tender = Tender.builder()
                .tenderNo(dto.getTenderNo())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .value(dto.getValue())
                .publishedDate(LocalDate.now())
                .closingDate(dto.getClosingDate())
                .status(Tender.Status.OPEN)
                .build();
        return convertToDto(tenderRepository.save(tender));
    }

    @Transactional
    public TenderDto updateTender(TenderDto dto) {
        Tender tender = tenderRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tender not found with id: " + dto.getId()));
        if (dto.getTitle() != null)
            tender.setTitle(dto.getTitle());
        if (dto.getDescription() != null)
            tender.setDescription(dto.getDescription());
        if (dto.getCategory() != null)
            tender.setCategory(dto.getCategory());
        if (dto.getValue() != null)
            tender.setValue(dto.getValue());
        if (dto.getClosingDate() != null)
            tender.setClosingDate(dto.getClosingDate());
        return convertToDto(tenderRepository.save(tender));
    }

    @Transactional
    public TenderDto closeTender(Long id) {
        Tender tender = tenderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tender not found with id: " + id));
        tender.setStatus(Tender.Status.CLOSED);
        return convertToDto(tenderRepository.save(tender));
    }

    @Transactional
    public void deleteTender(Long id) {
        if (!tenderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tender not found with id: " + id);
        }
        tenderRepository.deleteById(id);
    }

    public TenderDto convertToDto(Tender tender) {
        return TenderDto.builder()
                .id(tender.getId())
                .tenderNo(tender.getTenderNo())
                .title(tender.getTitle())
                .description(tender.getDescription())
                .category(tender.getCategory())
                .value(tender.getValue())
                .publishedDate(tender.getPublishedDate())
                .closingDate(tender.getClosingDate())
                .status(tender.getStatus())
                .build();
    }
}
