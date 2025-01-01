package de.spricom.zaster.architecture;

import de.spricom.zaster.ZasterApplication;
import jakarta.persistence.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dessertj.modules.ModuleRegistry;
import org.dessertj.modules.fixed.JavaModules;
import org.dessertj.partitioning.ClazzPredicates;
import org.dessertj.slicing.Classpath;
import org.dessertj.slicing.Root;
import org.dessertj.slicing.Slice;
import org.dessertj.util.Predicates;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dessertj.assertions.SliceAssertions.assertThatSlice;
import static org.dessertj.assertions.SliceAssertions.assertThatSlices;

public class BackendArchitectureTest {
    private static final Classpath cp = new Classpath();
    private static final ModuleRegistry mr = new ModuleRegistry(cp);
    private static final JavaModules java = new JavaModules(mr);

    private final Root backend = cp.rootOf(ZasterApplication.class);

    private final Slice dtos = backend.slice("..zaster.dtos..*");
    private final Slice endpoints = backend.slice("..zaster.endpoints..*");
    private final Slice entities = backend.slice("..zaster.entities..*");
    private final Slice enums = backend.slice("..zaster.enums..*");
    private final Slice importing = backend.slice("..zaster.importing..*");
    private final Slice repository = backend.slice("..zaster.repository..*");
    private final Slice repositoryInferfaces = repository.slice("..repository.*").slice(ClazzPredicates.INTERFACE);
    private final Slice security = backend.slice("..zaster.security..*");

    private final Slice log4j = cp.sliceOf(Logger.class, LogManager.class);

    @Test
    @Disabled
    void ensureNoPackageCycles() {
        assertThatSlices(backend.partitionByPackage()).areCycleFree();
    }

    @Test
    @Disabled
    void ensureNoDuplicates() {
        assertThat(cp.duplicates().minus("module-info").getClazzes()).isEmpty();
    }

    @Test
    void enums() {
        assertThatSlice(enums).usesOnly(java.base);
    }

    @Test
    void dtos() {
        Slice annotations = cp.slice("dev.hilla..*").slice(ClazzPredicates.ANNOTATION)
                .plus(cp.sliceOf(NonNullApi.class));
        assertThatSlice(dtos).usesOnly(java.base, annotations, enums);
    }

    @Test
    void entities() {
        Slice jpa = cp.rootOf(Entity.class).slice(Predicates.or(ClazzPredicates.ANNOTATION, ClazzPredicates.ENUM));
        Slice hibernate = cp.sliceOf(CreationTimestamp.class, UpdateTimestamp.class);
        assertThatSlice(entities.minus(ClazzPredicates.matchesSimpleName(".*_"))).usesOnly(java.base, jpa, hibernate,
                enums);
    }

    @Test
    void repository() {
        Slice spring = cp.sliceOf(Service.class, Autowired.class, Transactional.class);
        Slice springData = cp.slice("org.springframework.data..*")
                .slice(Predicates.or(ClazzPredicates.ANNOTATION, ClazzPredicates.ENUM, ClazzPredicates.INTERFACE));
        Slice jpa = cp.rootOf(Entity.class);
        Slice jackson = cp.slice("com.fasterxml.jackson..*");
        assertThatSlice(repository).usesOnly(java.base, springData, jpa, spring, log4j, jackson, entities, enums);
    }

    @Test
    @Disabled
    void security() {
        assertThatSlices(security).useOnly(java.base, cp.slice("org.springframework..*"),
                cp.slice("com.vaadin..security..*"), enums, entities.slice("..settings.*"), repositoryInferfaces);
    }
}
