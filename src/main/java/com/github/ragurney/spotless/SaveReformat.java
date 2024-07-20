package com.github.ragurney.spotless;

import com.github.ragurney.spotless.actions.ReformatCodeProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Reformat when saved
 * @author ziy
 * @version 1.0
 * @date 2024-07-19 下午6:41
 */
public class SaveReformat implements BulkFileListener {

    @Override
    public void before(@NotNull List<? extends VFileEvent> events) {
        events.stream()
                .filter(event -> event.isValid() && event.isFromSave())
                .forEach(event -> {
                    // save event
                    VirtualFile file = event.getFile();
                    if (file == null) {
                        return;
                    }
                    Project project = ProjectLocator.getInstance()
                            .guessProjectForFile(file);
                    PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                    // reformat  spotless hook
                    new ReformatCodeProcessor(psiFile).run();
                });
        BulkFileListener.super.before(events);
    }

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        BulkFileListener.super.after(events);
    }
}
