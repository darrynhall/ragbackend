package org.aero.ingestion.entity;

import java.io.Serializable;

public record FileDeletionEvent(String filename, String badge) implements Serializable {

}
