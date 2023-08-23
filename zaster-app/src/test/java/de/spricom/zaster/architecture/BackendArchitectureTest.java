package de.spricom.zaster.architecture;

import de.spricom.zaster.ZasterApplication;
import org.dessertj.modules.ModuleRegistry;
import org.dessertj.modules.fixed.JavaModules;
import org.dessertj.partitioning.ClazzPredicates;
import org.dessertj.slicing.Classpath;
import org.dessertj.slicing.Root;
import org.dessertj.slicing.Slice;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNullApi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dessertj.assertions.SliceAssertions.assertThatSlice;
import static org.dessertj.assertions.SliceAssertions.assertThatSlices;

public class BackendArchitectureTest {
    private static final Classpath cp = new Classpath();
    private static final ModuleRegistry mr = new ModuleRegistry(cp);
    private static final JavaModules java = new JavaModules(mr);

    private Root backend = cp.rootOf(ZasterApplication.class);

    private Slice dtos = backend.slice("..zaster.dtos..*");
    private Slice endpoints = backend.slice("..zaster.endpoints..*");
    private Slice entities = backend.slice("..zaster.entities..*");
    private Slice enums = backend.slice("..zaster.enums..*");
    private Slice importing = backend.slice("..zaster.importing..*");
    private Slice repository = backend.slice("..zaster.repository..*");
    private Slice security = backend.slice("..zaster.security..*");

    @Test
    void ensureNoPackageCycles() {
        assertThatSlices(backend.partitionByPackage()).areCycleFree();
    }

    @Test
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
}
