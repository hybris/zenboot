package org.zenboot.portal.processing


abstract class AbstractRankableFile {

    private Set qualifier
    private File file

    AbstractRankableFile(File file) {
        this.file = file
    }

    abstract protected Set qualify(String name)

    Set getQualifier() {
        if (!this.qualifier) {
            this.qualifier = this.qualify(this.file.name)
        }
        return this.qualifier.asImmutable()
    }

    File getFile() {
        return this.file
    }

    boolean isDefault() {
        return (this.getQualifier().size() == 0)
    }

    int getRank(List attributes) {
        int weight
        if (this.getQualifier().size() == 0) {
            //no specialization: weight=0
            weight = 0
        } else {
            //count the number of fitting attributes
            int hits = this.getQualifier().count { attributes.contains(it) }
            //boost hits
            weight = hits * 1000

            //non fitting attributes reduce the rank
            weight -= (this.getQualifier().size() - hits)
        }
        return weight
    }

}