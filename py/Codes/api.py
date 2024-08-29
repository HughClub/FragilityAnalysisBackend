import traceback
import os.path as osp
from enum import Enum
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

# from Main import Step_1, Step_2, Step_3, Step_4, Step_5
# region step mock
import time
def _step_mock(i, o, n, t):
    print(f"Step {n}")
    time.sleep(t)
Step_1 = lambda i, o: _step_mock(i, o, 1, 2)
Step_2 = lambda i, o: _step_mock(i, o, 2, 120)
Step_3 = lambda i, o: _step_mock(i, o, 3, 30)
Step_4 = lambda i, o: _step_mock(i, o, 4, 2)
Step_5 = lambda i, o: _step_mock(i, o, 5, 2)
# endregion

AnalysisRoot = "D:/upload/"
StepFnList = [globals()[f"Step_{i}"] for i in range(1, 6)]

app = FastAPI()

origins = [
    "http://localhost:*",
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class ResultCode(Enum):
    SUCCESS = 200
    STEP_INVALID = 400
    TASK_INVALID = 401
    TASK_BURST = 402
    DRY_RUN = 201
    HELLO = 202

    def result(self):
        return {"code": self.value, "message": MESSAGE[self]}


MESSAGE = {
    ResultCode.SUCCESS: "Success",
    ResultCode.STEP_INVALID: "Invalid step",
    ResultCode.TASK_INVALID: "Invalid task",
    ResultCode.TASK_BURST: "Runtime error while processing",
    ResultCode.DRY_RUN: "Dry run success",
    ResultCode.HELLO: "Welcome to Fragility Analysis Internal API",
}


def get_step_parameter(task_id: int, output: bool = True):
    folder = "output" if output else "input"
    return osp.join(AnalysisRoot, str(task_id), folder)


def check_task_step(task_id: int, step_id: int):
    if step_id not in [1, 2, 3, 4, 5]:
        return ResultCode.STEP_INVALID
    if step_id > 1 and not osp.exists(get_step_parameter(task_id)):
        return ResultCode.TASK_INVALID
    return ResultCode.SUCCESS  # new task comes in

def core_run_step(task_id: int, step_id: int):
    StepFnList[step_id-1](
        get_step_parameter(task_id, False),
        get_step_parameter(task_id),
    )

def core_process(task_id: int, step_id: int):
    check_result = check_task_step(task_id, step_id)
    if check_result != ResultCode.SUCCESS:
        return check_result.result()
    try:
        core_run_step(task_id, step_id)
    except Exception as e:
        # print error with trace
        traceback.print_exc()
        return ResultCode.TASK_BURST.result()
    return ResultCode.SUCCESS.result()


@app.get("/")
async def api_hello():
    return ResultCode.HELLO.result()

@app.get("/step/{task_id}/{step_id}")
def api_process(task_id: int, step_id: int):
    return core_process(task_id, step_id)