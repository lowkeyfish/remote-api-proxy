<script setup>
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import {
  EditorView,
  keymap,
  highlightSpecialChars,
  drawSelection,
  highlightActiveLine,
  dropCursor,
  rectangularSelection,
  crosshairCursor,
  lineNumbers,
  highlightActiveLineGutter
} from '@codemirror/view'
import { EditorState } from '@codemirror/state'
import {
  defaultHighlightStyle,
  syntaxHighlighting,
  indentOnInput,
  bracketMatching,
  foldGutter,
  foldKeymap
} from '@codemirror/language'
import {
  defaultKeymap,
  history,
  historyKeymap,
  indentWithTab,
  indentLess
} from '@codemirror/commands'
import {
  autocompletion,
  completionKeymap,
  closeBrackets,
  closeBracketsKeymap
} from '@codemirror/autocomplete'
import { json } from '@codemirror/lang-json'

let model = defineModel()
let props = defineProps({
  readonly: {
    type: Boolean,
    default: false
  },
  extensions: {
    type: Array,
    required: true
  }
})

let editor = ref(null)
let editorView = null

onMounted(() => {
  let editorState = EditorState.create({
    doc: model.value,
    extensions: [
      EditorView.editable.of(!props.readonly),
      lineNumbers(),
      highlightActiveLineGutter(),
      highlightSpecialChars(),
      history(),
      foldGutter(),
      drawSelection(),
      dropCursor(),
      EditorState.allowMultipleSelections.of(true),
      indentOnInput(),
      syntaxHighlighting(defaultHighlightStyle, { fallback: true }),
      bracketMatching(),
      closeBrackets(),
      autocompletion(),
      rectangularSelection(),
      crosshairCursor(),
      highlightActiveLine(),
      keymap.of([
        ...closeBracketsKeymap,
        ...defaultKeymap,
        ...historyKeymap,
        ...foldKeymap,
        ...completionKeymap,
        indentWithTab,
        indentLess
      ]),
      [...props.extensions],
      EditorView.updateListener.of((update) => {
        if (update.docChanged) {
          model.value = editorView.state.doc.toString()
        }
      })
    ]
  })

  editorView = new EditorView({
    state: editorState,
    parent: editor.value
  })
})

onBeforeUnmount(() => {
  if (editorView) {
    editorView.destroy()
  }
})

watch(model, (newValue, oldValue) => {
  if (editorView) {
    const currentValue = editorView.state.doc.toString()
    if (newValue !== currentValue) {
      editorView.dispatch({
        changes: {
          from: 0,
          to: currentValue.length,
          insert: newValue
        }
      })
    }
  }
})
</script>

<template>
  <div ref="editor" class="cm-editor-container"></div>
</template>

<style lang="scss"></style>
