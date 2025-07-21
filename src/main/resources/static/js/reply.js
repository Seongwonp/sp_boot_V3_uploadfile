let currentPage = 1;
const pageSize = 10;
let hasNext = true;

document.addEventListener("DOMContentLoaded", () => {
    console.log("DOM fully loaded. Initializing reply functions.");

    loadReplyList();

    document.getElementById("replySubmitBtn").addEventListener("click", async () => {
        const replyWriter = document.getElementById("replyWriter").value.trim();
        const replyText = document.getElementById("replyText").value.trim();
        const bno = document.getElementById("currentBno").value;

        console.log("Submitting new reply:", { bno, replyWriter, replyText });

        if (!replyWriter || !replyText) {
            alert("Please enter both your name and your comment!");
            return;
        }

        try {
            await axios.post("/api/replies", { bno, replyWriter, replyText });
            console.log("Reply successfully posted.");
            document.getElementById("replyWriter").value = "";
            document.getElementById("replyText").value = "";
            currentPage = 1;
            hasNext = true;
            document.getElementById("replyListBox").innerHTML = "";
            document.getElementById("loadMoreBtn").style.display = "block";
            await loadReplyList();
        } catch (err) {
            console.error("Failed to submit reply:", err);
            alert("Error submitting reply. Please try again.");
        }
    });

    document.getElementById("loadMoreBtn").addEventListener("click", loadReplyList);
});

async function loadReplyList() {
    const bno = document.getElementById("currentBno").value;
    console.log("Loading replies for bno =", bno, "page =", currentPage);

    if (!hasNext) {
        console.log("No more comments to load.");
        document.getElementById("loadMoreBtn").style.display = "none";
        return;
    }

    try {
        const res = await axios.get(`/api/replies/list/${bno}?page=${currentPage}&size=${pageSize}`);
        const data = res.data;
        console.log("Server reply data:", data);

        const listBox = document.getElementById("replyListBox");

        if (!data.dtoList || data.dtoList.length === 0 && currentPage === 1) {
            console.log("No comments found for this post.");
            listBox.innerHTML = "<p class='text-muted'>No comments yet.</p>";
            document.getElementById("loadMoreBtn").style.display = "none";
            return;
        }

        data.dtoList.forEach(reply => {
            console.log("Rendering reply:", reply);

            const edited = reply.replyDate !== reply.modDate;
            const editLabel = edited ? `<span class="text-warning small ms-2">(Edited)</span>` : "";

            const html = `
        <div class="card mb-2">
          <div class="card-body">
            <div class="d-flex justify-content-between">
              <div>
                <strong>${reply.replyWriter}</strong>
                <span class="text-muted small">Posted on ${reply.replyDate?.substring(0, 10)}</span>
                ${editLabel}
              </div>
              <div>
                <button class="btn btn-sm btn-outline-secondary me-1" onclick="showEditForm(${reply.rno}, \`${reply.replyText}\`)">Edit</button>
                <button class="btn btn-sm btn-outline-danger" onclick="deleteReply(${reply.rno})">Delete</button>
              </div>
            </div>
            <p class="mt-2 mb-0">${reply.replyText}</p>
            <div id="editFormBox-${reply.rno}" class="mt-2" style="display:none;"></div>
          </div>
        </div>
      `;
            listBox.innerHTML += html;
        });

        currentPage++;
        console.log(data.next);
        hasNext = data.next;
        if (!hasNext) {
            document.getElementById("loadMoreBtn").style.display = "none";
        }
    } catch (err) {
        console.error("Failed to load replies:", err);
        alert("Could not load replies.");
    }
}

function showEditForm(rno, currentText) {
    console.log("Showing edit form for rno =", rno);

    const editBox = document.getElementById(`editFormBox-${rno}`);
    editBox.innerHTML = `
    <div class="card border-0 shadow-sm mb-3 bg-white rounded">
      <div class="card-body">
        <div class="mb-2">
          <label for="editText-${rno}" class="form-label fw-semibold">Edit Comment</label>
          <textarea class="form-control" id="editText-${rno}" rows="3" placeholder="Update your comment here...">${currentText}</textarea>
        </div>
        <div class="d-flex justify-content-end gap-2">
          <button class="btn btn-outline-success" onclick="updateReply(${rno})">
            <i class="bi bi-check-circle-fill"></i> Save
          </button>
          <button class="btn btn-outline-secondary" onclick="cancelEdit(${rno})">
            <i class="bi bi-x-circle-fill"></i> Cancel
          </button>
        </div>
      </div>
    </div>
  `;
    editBox.style.display = "block";
}

function cancelEdit(rno) {
    console.log("Cancelled edit form for rno =", rno);
    const editBox = document.getElementById(`editFormBox-${rno}`);
    editBox.style.display = "none";
    editBox.innerHTML = "";
}

async function updateReply(rno) {
    const newText = document.getElementById(`editText-${rno}`).value.trim();
    console.log("Updating reply rno =", rno, "with newText =", newText);

    if (!newText) {
        alert("Comment cannot be empty.");
        return;
    }

    try {
        await axios.put(`/api/replies/${rno}`, { replyText: newText });
        console.log("Comment updated successfully.");
        currentPage = 1;
        hasNext = true;
        document.getElementById("replyListBox").innerHTML = "";
        document.getElementById("loadMoreBtn").style.display = "block";
        await loadReplyList();
    } catch (err) {
        console.error("Failed to update reply:", err);
        alert("Could not update reply.");
    }
}

async function deleteReply(rno) {
    console.log("Attempting to delete reply rno =", rno);

    if (!confirm("Are you sure you want to delete this comment?")) return;

    try {
        await axios.delete(`/api/replies/${rno}`);
        console.log("Reply deleted.");
        currentPage = 1;
        hasNext = true;
        document.getElementById("replyListBox").innerHTML = "";
        document.getElementById("loadMoreBtn").style.display = "block";
        await loadReplyList();
    } catch (err) {
        console.error("Failed to delete reply:", err);
        alert("Could not delete reply.");
    }
}
