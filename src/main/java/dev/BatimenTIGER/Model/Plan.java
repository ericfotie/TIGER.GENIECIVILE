package dev.BatimenTIGER.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_plans")
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomDocument;

    @Column(nullable = false)
    private String fichierUrl;

    private String indiceRevision; // ex: Rev A
    private String typeTechnique; // ex: Béton Armé, VRD

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projet_id")
    private Projet projet;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomDocument() {
        return nomDocument;
    }

    public void setNomDocument(String nomDocument) {
        this.nomDocument = nomDocument;
    }

    public String getFichierUrl() {
        return fichierUrl;
    }

    public void setFichierUrl(String fichierUrl) {
        this.fichierUrl = fichierUrl;
    }

    public String getIndiceRevision() {
        return indiceRevision;
    }

    public void setIndiceRevision(String indiceRevision) {
        this.indiceRevision = indiceRevision;
    }

    public String getTypeTechnique() {
        return typeTechnique;
    }

    public void setTypeTechnique(String typeTechnique) {
        this.typeTechnique = typeTechnique;
    }

    public Projet getProjet() {
        return projet;
    }

    public void setProjet(Projet projet) {
        this.projet = projet;
    }
// Getters and Setters
}
