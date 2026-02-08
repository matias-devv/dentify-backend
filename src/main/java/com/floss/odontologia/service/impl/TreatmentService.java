package com.floss.odontologia.service.impl;

import com.floss.odontologia.enums.TreatmentStatus;
import com.floss.odontologia.model.Patient;
import com.floss.odontologia.model.Product;
import com.floss.odontologia.model.Treatment;
import com.floss.odontologia.repository.ITreatmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ITreatmentService implements ITreatmentRepository {

    private final ITreatmentRepository treatmentRepository;


    @Override
    public Optional<Treatment> findByPatientAndProductAndTreatmentStatus(Patient patient, Product product, TreatmentStatus treatmentStatus) {
        return Optional.empty();
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Treatment> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Treatment> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Treatment> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Treatment getOne(Long aLong) {
        return null;
    }

    @Override
    public Treatment getById(Long aLong) {
        return null;
    }

    @Override
    public Treatment getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Treatment> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Treatment> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Treatment> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Treatment> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Treatment> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Treatment> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Treatment, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Treatment> S save(S entity) {
        return null;
    }

    @Override
    public <S extends Treatment> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Treatment> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<Treatment> findAll() {
        return List.of();
    }

    @Override
    public List<Treatment> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(Treatment entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Treatment> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Treatment> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Treatment> findAll(Pageable pageable) {
        return null;
    }
}
