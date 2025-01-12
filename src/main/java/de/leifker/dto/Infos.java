package de.leifker.dto;

import java.util.List;

public record Infos(List<String> info) implements SpeakerPart {
}
