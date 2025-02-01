package com.knowledge.database.exercise.base;

import org.junit.jupiter.api.BeforeEach;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.model.OntologyConfigurator;
import org.semanticweb.owlapi.model.PrefixManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public abstract class TestBase {
	protected static OntologyConfigurator masterConfigurator;
	protected OWLOntologyManager ontologyManager;
	protected OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();

	@BeforeEach
	public void setup() {
		masterConfigurator = new OntologyConfigurator();
		masterConfigurator.buildLoaderConfiguration();
		ontologyManager = setupManager();
	}

	protected OWLOntology ontologyFromClasspathFile(String fileName) {
		return ontologyFromClasspathFile(fileName, config);
	}

	protected OWLOntology ontologyFromClasspathFile(String fileName,
	                                                OWLOntologyLoaderConfiguration configuration) {
		try (InputStream in = getClass().getResourceAsStream('/' + fileName)) {
			return ontologyManager.loadOntologyFromOntologyDocument(new StreamDocumentSource(in), configuration);
		} catch (OWLOntologyCreationException | IOException ex) {
			throw new OWLRuntimeException(ex);
		}
	}

	protected static OWLOntologyManager setupManager() {
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		man.setOntologyConfigurator(masterConfigurator);
		return man;
	}

	protected void addABoxIndividual(OWLDataFactory df, PrefixManager pm, OWLOntology ontology, String owlClassSuffix, List<String> individuals) {
		OWLObjectProperty roleProperty = df.getOWLObjectProperty(new StringBuilder().append(":").append("hasName").toString(), pm);
		OWLClass owlClass = df.getOWLClass(new StringBuilder().append(":").append(owlClassSuffix).toString(), pm);
		for (String individual : individuals) {
			OWLNamedIndividual named_a = df.getOWLNamedIndividual(new StringBuilder().append(":").append(individual).toString(), pm);
			OWLEquivalentClassesAxiom has_name = df.getOWLEquivalentClassesAxiom(owlClass, df.getOWLObjectHasValue(roleProperty, named_a));
			ontologyManager.addAxiom(ontology, has_name);
		}
	}

	protected void addABoxRole(OWLDataFactory df, PrefixManager pm, OWLOntology ontology, String property,
	                           String quantification, List<String> individuals) {
		OWLObjectProperty roleProperty = df.getOWLObjectProperty(new StringBuilder().append(":").append(property).toString(), pm);

		OWLClass a = df.getOWLClass(new StringBuilder().append(":").append(quantification).toString(), pm);
		for (String individual : individuals) {
			OWLClass b = df.getOWLClass(new StringBuilder().append(":").append(individual).toString(), pm);

			OWLEquivalentClassesAxiom roleAssertion =
					df.getOWLEquivalentClassesAxiom(a, df.getOWLObjectSomeValuesFrom(roleProperty, b));

			ontologyManager.addAxiom(ontology, roleAssertion);
		}
	}

	protected void addABoxRoleIntLiteral(OWLDataFactory df, PrefixManager pm, OWLOntology ontology, String property,
	                                     String quantification, List<String> individuals, OWLDatatype datatype) {
		OWLDataProperty roleProperty = df.getOWLDataProperty(new StringBuilder().append(":").append(property).toString(), pm);

		OWLClass a = df.getOWLClass(new StringBuilder().append(":").append(quantification).toString(), pm);
		for (String individual : individuals) {
			OWLLiteral literal = df.getOWLLiteral(individual, datatype);

			OWLEquivalentClassesAxiom roleAssertion =
					df.getOWLEquivalentClassesAxiom(a, df.getOWLDataHasValue(roleProperty, literal));

			ontologyManager.addAxiom(ontology, roleAssertion);
		}
	}
}
