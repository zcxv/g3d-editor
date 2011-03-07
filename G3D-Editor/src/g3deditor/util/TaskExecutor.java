package g3deditor.util;

public final class TaskExecutor
{
	private final TaskExecutionHelper[] _helper;
	private final TaskQueue _taskQueue;
	private final TaskCompleteListener _taskCompleteListener;
	
	public TaskExecutor(int threads)
	{
		_helper = new TaskExecutionHelper[threads];
		_taskQueue = new TaskQueue();
		_taskCompleteListener = new TaskCompleteListener();
		
		while (threads-- > 0)
		{
			_helper[threads] = new TaskExecutionHelper(_taskQueue, _taskCompleteListener);
		}
	}
	
	public final void init()
	{
		for (int i = _helper.length; i-- > 0;)
		{
			_helper[i].start();
		}
	}
	
	public final void dispose()
	{
		for (int i = _helper.length; i-- > 0;)
		{
			_helper[i].interrupt();
		}
	}
	
	public final void execute(final Runnable[] tasks, final int size)
	{
		_taskCompleteListener.setTasksRemaining(size);
		
		synchronized (_taskQueue)
		{
			_taskQueue.offer(tasks, size);
			_taskQueue.notifyAll();
		}
		
		_taskCompleteListener.waitForComplete();
	}
	
	private static final class TaskCompleteListener
	{
		private int _tasksRemaining;
		
		public TaskCompleteListener()
		{
			
		}
		
		public final void setTasksRemaining(final int tasksRemaining)
		{
			_tasksRemaining = tasksRemaining;
		}
		
		public final void onTaskCompleted()
		{
			synchronized (this)
			{
				if (--_tasksRemaining == 0)
					notify();
			}
		}
		
		public final void waitForComplete()
		{
			try
			{
				synchronized (this)
				{
					while (_tasksRemaining > 0)
					{
						wait();
					}
				}
			}
			catch (final InterruptedException e)
			{
				
			}
		}
	}
	
	private static final class TaskExecutionHelper extends Thread
	{
		private final TaskQueue _taskQueue;
		private final TaskCompleteListener _taskCompleteListener;
		
		public TaskExecutionHelper(final TaskQueue taskQueue, final TaskCompleteListener taskCompleteListener)
		{
			_taskQueue = taskQueue;
			_taskCompleteListener = taskCompleteListener;
		}
		
		@Override
		public final void run()
		{
			Runnable task;
			
			try
			{
				while (!Thread.interrupted())
				{
					synchronized (_taskQueue)
					{
						while (_taskQueue.isEmpty())
						{
							_taskQueue.wait();
						}
						task = _taskQueue.poll();
					}
					
					try
					{
						task.run();
					}
					finally
					{
						_taskCompleteListener.onTaskCompleted();
					}
				}
			}
			catch (final InterruptedException e)
			{
				
			}
		}
	}
	
	private static final class TaskQueue
	{
		private Runnable[] _tasks;
		private int _size;
		
		public TaskQueue()
		{
			
		}
		
		public final void offer(final Runnable[] tasks, final int size)
		{
			_tasks = tasks;
			_size = size;
		}
		
		public final Runnable poll()
		{
			return _tasks[--_size];
		}
		
		public final boolean isEmpty()
		{
			return _size == 0;
		}
	}
}