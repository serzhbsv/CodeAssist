package com.tyron.completion.java.provider;

import com.tyron.completion.java.compiler.CompileTask;
import com.tyron.completion.java.compiler.JavaCompilerService;
import com.tyron.completion.java.util.ActionUtil;
import com.tyron.completion.java.util.CompletionItemFactory;
import com.tyron.completion.model.CompletionItem;
import com.tyron.completion.model.CompletionList;

import org.openjdk.javax.lang.model.element.Element;
import org.openjdk.javax.lang.model.element.VariableElement;
import org.openjdk.javax.lang.model.type.TypeMirror;
import org.openjdk.source.tree.VariableTree;
import org.openjdk.source.util.TreePath;
import org.openjdk.source.util.Trees;

import java.util.List;

/**
 * Responsive for suggesting unique variable names. If a variable already exists, it is appended
 * with a number. If the existing variable ends with a number one is added to it.
 */
public class VariableNameCompletionProvider extends BaseCompletionProvider {

    public VariableNameCompletionProvider(JavaCompilerService service) {
        super(service);
    }

    @Override
    public CompletionList complete(CompileTask task,
                                   TreePath path,
                                   String partial,
                                   boolean endsWithParen) {
        Element element = Trees.instance(task.task).getElement(path);
        if (element == null) {
            return CompletionList.EMPTY;
        }
        TypeMirror type = element.asType();
        if (type == null) {
            return CompletionList.EMPTY;
        }

        List<String> names = ActionUtil.guessNamesFromType(type);
        if (names.isEmpty()) {
            return CompletionList.EMPTY;
        }

        CompletionList list = new CompletionList();
        for (String name : names) {
            while (ActionUtil.containsVariableAtScope(name, task, path)) {
                name = ActionUtil.getVariableName(name);
            }
            CompletionItem item = CompletionItemFactory.item(name);
            list.items.add(item);
        }
        return list;
    }
}