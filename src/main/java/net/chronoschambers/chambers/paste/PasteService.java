package net.chronoschambers.chambers.paste;

import net.chronoschambers.chambers.paste.model.PasteContext;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Handles schematic paste operations.
 */
public interface PasteService {

    /**
     * Pastes a schematic using the given context.
     *
     * @param context the paste context
     * @param callback called with the paste result, or empty if the paste fails
     */
    void paste(@NotNull PasteContext context, @NotNull Consumer<Optional<PasteResult>> callback);

}
