package com.knowledge.database.exercise.base.db1;

import com.knowledge.database.exercise.base.TestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.io.StreamDocumentTarget;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@SpringBootTest
class DbTest extends TestBase {

	@Test
	void testDb2() throws OWLOntologyStorageException, IOException, URISyntaxException {
		OWLOntology ontology = ontologyFromClasspathFile(TestFileNames.DB2);
		String ontName = ontology.getOntologyID().getOntologyIRI().get().toString();

		OWLDataFactory df = ontologyManager.getOWLDataFactory();
		PrefixManager pm = new DefaultPrefixManager(TestFileNames.DB2_OWL);

		log.info("Ontology: {}", ontName);
		log.info("Ontology loaded: {}", ontology.getOntologyID());

		if (ontology.getAxioms().isEmpty()) {
			log.warn("WARNING: The ontology is empty!");
		} else {
			log.info("Ontology syntax is valid.");
		}

		{
			// Add some Men axioms.
			addABoxIndividual(df, pm, ontology, "Men", List.of("Beren", "Beren", "Hador"));
		}

		{
			// Add some Men axioms.
			addABoxIndividual(df, pm, ontology, "Hobbits", List.of("Frodo", "Merry", "Pippin", "Bilbo", "Gimli"));
		}

		{
			// Add some Dwarves axioms.
			addABoxIndividual(df, pm, ontology, "Dwarves", List.of("Gimli", "Thiorin", "Balin", "Dwalvin", "Oin", "Celeborn"));
		}

		{
			// Add some Light Elves axioms.
			addABoxIndividual(df, pm, ontology, "Light_Elves", List.of("Galadriel", "Elrond"));
		}

		{
			// Add some Dark Elves axioms.
			addABoxIndividual(df, pm, ontology, "Dark_Elves", List.of("Legolas", "Thranduil"));
		}

		{
			// Add some Orcs axioms.
			addABoxIndividual(df, pm, ontology, "Orcs", List.of("Azog", "Bolg", "Gothmog"));
		}

		{
			// Add some Uruk-hai axioms.
			addABoxIndividual(df, pm, ontology, "Uruk-hai", List.of("Lurtz", "Ugluk"));
		}

		{
			addABoxRole(df, pm, ontology, "inhabitatedBy", "Shire",
					List.of("Frodo", "Sam", "Merry", "Pippin", "Bilbo"));
		}

		{
			addABoxRole(df, pm, ontology, "inhabitatedBy", "Gondor",
					List.of("Aragon", "Boromir", "Faramir", "Theoden", "Eomer"));
		}

		{
			addABoxRole(df, pm, ontology, "inhabitatedBy", "Mordor",
					List.of("Azog", "Bolg", "Gothmog", "Lurtz", "Ugluk"));
		}

		{
			addABoxRole(df, pm, ontology, "inhabitatedBy", "Isengard",
					List.of("Lurtz", "Ugluk"));
		}

		{
			addABoxRole(df, pm, ontology, "bredIn", "Isengard",
					List.of("Lurtz", "Ugluk"));
		}

		{
			addABoxRole(df, pm, ontology, "rivalryWith", "Elves", List.of("Orcs"));
			addABoxRole(df, pm, ontology, "rivalryWith", "Dwarves", List.of("Orcs"));
			addABoxRole(df, pm, ontology, "rivalryWith", "Men", List.of("Orcs"));
			addABoxRole(df, pm, ontology, "rivalryWith", "Gondor", List.of("Mordor"));
			addABoxRole(df, pm, ontology, "rivalryWith", "Shire", List.of("Isengard"));

			addABoxRoleIntLiteral(df, pm, ontology, "lifespan", "Aragorn", List.of("210"), OWL2Datatype.XSD_INT.getDatatype(df));
			addABoxRoleIntLiteral(df, pm, ontology, "lifespan", "Frodo", List.of("100"), OWL2Datatype.XSD_INT.getDatatype(df));
			addABoxRoleIntLiteral(df, pm, ontology, "lifespan", "Gimli", List.of("250"), OWL2Datatype.XSD_INT.getDatatype(df));
			addABoxRoleIntLiteral(df, pm, ontology, "lifespan", "Azog", List.of("80"), OWL2Datatype.XSD_INT.getDatatype(df));

			addABoxRole(df, pm, ontology, "inhabitatedBy", "Isengard", List.of("Lurtz", "Ugluk"));
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ontologyManager.saveOntology(ontology, new StreamDocumentTarget(baos));

		log.info(baos.toString());

		ontologyManager.saveOntology(ontology, Files.newOutputStream(Paths.get(TestFileNames.DB2_GEN)));
	}
}
