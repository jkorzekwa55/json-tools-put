// ── Navigation ───────────────────────────────────────────────────
document.querySelectorAll('.nav-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));
        document.querySelectorAll('.panel').forEach(p => p.classList.remove('active'));
        btn.classList.add('active');
        document.getElementById('panel-' + btn.dataset.panel).classList.add('active');
        hideResult();
    });
});

// ── Pipeline chip toggle ─────────────────────────────────────────
function toggleAction(chip) {
    chip.classList.toggle('selected');
    const sel = [...document.querySelectorAll('.action-chip.selected')].map(c => c.dataset.action);
    document.getElementById('pipe-keep-field').style.display = sel.includes('filter') ? '' : 'none';
    document.getElementById('pipe-excl-field').style.display = sel.includes('exclude-keys') ? '' : 'none';
}

// ── Helpers ──────────────────────────────────────────────────────
function valid(el, emptyMessage = 'JSON input cannot be empty.', invalidMessage = 'Invalid JSON — fix the red textarea first.') {
    if (!el.value.trim()) {el.classList.add('err'); showText(emptyMessage, true); return false;}
    try {
        JSON.parse(el.value);el.classList.remove('err');
        return true;
    } catch { el.classList.add('err'); showText(invalidMessage, true); return false;}
}

function keys(str) {
    return str.split(',').map(s => s.trim()).filter(Boolean);
}

function dupKeys(parsedKeys) {
    const seen = new Set();
    const dup  = new Set();
    parsedKeys.forEach(key => {
        if (seen.has(key)) dup.add(key);
        else seen.add(key);
    });
    return Array.from(dup);
}

function warnDupKeys(inputId, parsedKeys) {
    const dup = dupKeys(parsedKeys);
    if (!dup.length) return;
    const input = document.getElementById(inputId);
    if (!input) return;
    input.setCustomValidity('Warning: duplicate keys entered: ' + dup.join(', '));
    input.reportValidity();
    setTimeout(() => { input.setCustomValidity(''); }, 3000);
}

function errorPos(error) {
    const match = error.message.match(/position (\d+)/i);
    return match ? Number(match[1]) : null;
}

function posToLineCol(text, position) {
    const beforeError = text.slice(0, position);
    const lines = beforeError.split('\n');
    return { line: lines.length, column: lines[lines.length - 1].length + 1 };
}

function liveValidJson(el) {
    if (!el.value.trim()) {
        el.classList.remove('err');
        el.title = '';
        return;
    }
    try {
        JSON.parse(el.value);
        el.classList.remove('err');
        el.title = 'Valid JSON';
    } catch (error) {
        el.classList.add('err');
        const position = errorPos(error);
        if (position === null) {el.title = 'Invalid JSON: ' + error.message; return; }
        const location = posToLineCol(el.value, position);
        el.title = `Invalid JSON at line ${location.line}, column ${location.column}.`;
    }
}

function spin(id, on) {
    const s = document.getElementById('sp-' + id);
    if (s) s.style.display = on ? 'inline-block' : 'none';
}

function showText(text, isErr) {
    const inner = document.getElementById('result-inner');
    inner.innerHTML = '';
    const pre = document.createElement('pre');
    pre.className = 'result-body' + (isErr ? ' err' : '');
    pre.textContent = text;
    inner.appendChild(pre);
    showCard(isErr);
}

function showDiff(diffs) {
    const inner = document.getElementById('result-inner');
    inner.innerHTML = '';
    if (!diffs || diffs.length === 0) {
        inner.innerHTML = '<div class="no-diff">✓ No differences found — the two JSONs are identical.</div>';
        showCard(false);
        return;
    }
    const wrap = document.createElement('div');
    wrap.className = 'diff-wrap';
    const table = document.createElement('table');
    table.className = 'diff';
    table.innerHTML = `<thead><tr>
        <th style="width:36px">#</th>
        <th style="width:80px">Status</th>
        <th>Left</th>
        <th>Right</th>
    </tr></thead>`;
    const tbody = document.createElement('tbody');
    diffs.forEach(d => {
        const hasLeft  = d.left  !== null && d.left  !== undefined && d.left  !== '';
        const hasRight = d.right !== null && d.right !== undefined && d.right !== '';
        let cls, tag;
        if (hasLeft && !hasRight)      { cls = 'removed'; tag = '<span class="tag-rem">− removed</span>'; }
        else if (!hasLeft && hasRight) { cls = 'added';   tag = '<span class="tag-add">+ added</span>';   }
        else                           { cls = 'changed'; tag = '<span class="tag-chg">~ changed</span>'; }
        const tr = document.createElement('tr');
        tr.className = cls;
        tr.innerHTML = `<td class="ln">${d.line}</td><td>${tag}</td><td>${esc(d.left)}</td><td>${esc(d.right)}</td>`;
        tbody.appendChild(tr);
    });
    table.appendChild(tbody);
    wrap.appendChild(table);
    inner.appendChild(wrap);
    showCard(false);
}

function showCard(isErr) {
    const card  = document.getElementById('result-card');
    const badge = document.getElementById('result-badge');
    card.classList.add('show');
    badge.textContent = isErr ? 'Error' : 'Result';
    badge.className = 'result-badge' + (isErr ? ' err' : '');
    card.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

function hideResult() {
    document.getElementById('result-card').classList.remove('show');
}

function esc(str) {
    if (str === null || str === undefined) return '<span style="color:var(--muted)">—</span>';
    return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

function copyResult() {
    const text = document.getElementById('result-inner').innerText;
    navigator.clipboard.writeText(text).catch(() => {});
    const b = document.querySelector('.copy-btn');
    b.textContent = 'Copied ✓';
    setTimeout(() => b.textContent = 'Copy', 1600);
}

async function post(url, body) {
    let r;
    try {
        r = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
    } catch {
        throw new Error('Cannot connect to the server. Make sure the json tools application is running.');
    }
    if (!r.ok) {
            const det = await errorMes(r);
            if (r.status === 400) throw new Error('Invalid request. Please check your JSON input and key values.');
            if (r.status === 404) throw new Error('Requested operation was not found.');
            if (r.status >= 500) throw new Error('Server error while processing JSON.');
            throw new Error('Request failed with status ' + r.status + (det ? ': ' + det : ''));
    }
    const ct = r.headers.get('content-type') || '';
    return ct.includes('application/json') ? r.json() : r.text();
}

async function errorMes(response) {
    const ct = response.headers.get('content-type') || '';
    try {
        if (ct.includes('application/json')) {
            const data = await response.json();
            return data.message || data.error || data.details || JSON.stringify(data);
        }
        return await response.text();
    } catch {
        return response.statusText;
    }
}

// ── Endpoints ────────────────────────────────────────────────────

async function doMinify() {
    const el = document.getElementById('minify-json');
    if (!valid(el)) return;
    spin('minify', true);
    try {
        const d = await post('/api/json/minify', { json: el.value });
        showText(typeof d === 'string' ? d : JSON.stringify(d), false);
    } catch(e) { showText(e.message, true); }
    finally { spin('minify', false); }
}

async function doPretty() {
    const el = document.getElementById('pretty-json');
    if (!valid(el)) return;
    spin('pretty', true);
    try {
        const d = await post('/api/json/pretty-print', { json: el.value });
        showText(typeof d === 'string' ? d : JSON.stringify(d, null, 2), false);
    } catch(e) { showText(e.message, true); }
    finally { spin('pretty', false); }
}

async function doFilter() {
    const el = document.getElementById('filter-json');
    const k  = keys(document.getElementById('filter-keys').value);
    if (!valid(el)) return;
    if (!k.length)  { showText('Enter at least one key to keep.', true); return; }
    warnDupKeys('filter-keys', k);
    spin('filter', true);
    try {
        const d = await post('/api/json/filter-keys', { json: el.value, keysToKeep: k });
        showText(JSON.stringify(d, null, 2), false);
    } catch(e) { showText(e.message, true); }
    finally { spin('filter', false); }
}

async function doExclude() {
    const el = document.getElementById('exclude-json');
    const k  = keys(document.getElementById('exclude-keys').value);
    if (!valid(el)) return;
    if (!k.length)  { showText('Enter at least one key to exclude.', true); return; }
    warnDupKeys('exclude-keys', k);
    spin('exclude', true);
    try {
        const d = await post('/api/json/exclude-keys', { json: el.value, keysToExclude: k });
        showText(JSON.stringify(d, null, 2), false);
    } catch(e) { showText(e.message, true); }
    finally { spin('exclude', false); }
}

async function doPipeline() {
    const el      = document.getElementById('pipe-json');
    const actions = [...document.querySelectorAll('.action-chip.selected')].map(c => c.dataset.action);
    if (!valid(el)) return;
    if (!actions.length) { showText('Pick at least one action.', true); return; }

    const body = { json: el.value, actions };
    if (actions.includes('filter')) {
        const k = keys(document.getElementById('pipe-keep').value);
        if (!k.length) { showText('Filter action selected — enter at least one key to keep.', true); return; }
        warnDupKeys('pipe-keep', k);
        body.keysToKeep = k;
    }
    if (actions.includes('exclude-keys')) {
        const k = keys(document.getElementById('pipe-excl').value);
        if (!k.length) { showText('Exclude-keys action selected — enter at least one key to exclude.', true); return; }
        warnDupKeys('pipe-excl', k);
        body.keysToExclude = k;
    }
    spin('pipeline', true);
    try {
        const d = await post('/api/json/transform', body);
        showText(typeof d === 'string' ? d : JSON.stringify(d, null, 2), false);
    } catch(e) { showText(e.message, true); }
    finally { spin('pipeline', false); }
}

async function doCompare() {
    const la = document.getElementById('compare-left');
    const ra = document.getElementById('compare-right');
    if (!la.value.trim() || !ra.value.trim()) {
        la.classList.toggle('err', !la.value.trim());
        ra.classList.toggle('err', !ra.value.trim());

        if (!la.value.trim() && !ra.value.trim()) showText('Both JSON inputs cannot be empty.', true);
        else if (!la.value.trim()) showText('Left JSON input cannot be empty.', true);
        else showText('Right JSON input cannot be empty.', true);
        return;
    }
    const leftOk = valid(la, '', 'Left input must be valid JSON.');
    const rightOk = valid(ra, '', 'Right input must be valid JSON.');
    if (!leftOk || !rightOk) return;
    spin('compare', true);
    try {
        const d = await post('/api/json/compare', { left: la.value, right: ra.value });
        showDiff(d.differences);
    } catch(e) { showText(e.message, true); }
    finally { spin('compare', false); }
}

document.querySelectorAll('textarea').forEach(el => {
    el.addEventListener('input', () => liveValidJson(el));
});
